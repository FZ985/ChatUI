package io.ai.chat.clientimpl

import io.ai.chat.client.AIClient
import io.ai.chat.utils.ALog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/**
 * by DAD FZ
 * 2026/8/21
 * desc：
 **/
abstract class ClientImpl : AIClient {
    val Component = "Api"
    val mainDispatcher = Dispatchers.Main
    val ERROR_SNIPPET_MAX_LENGTH = 240
    val MODEL_API_USER_AGENT = "AIT/1.0 (Android; OpenAI-Compatible Client)"

    val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun executeRequest(request: Request): Response {
        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            continuation.invokeOnCancellation {
                call.cancel()
            }
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response)
                }
            })
        }
    }

    fun buildRequest(
        baseUrl: String,
        apiKey: String,
        requestBody: String,
        isStreaming: Boolean
    ): Request {
        val url = resolveChatUrl(baseUrl)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        return Request.Builder()
            .url(url)
            .post(requestBody.toRequestBody(mediaType))
            .header("User-Agent", MODEL_API_USER_AGENT)
            .apply {
                if (isStreaming) {
                    addHeader("Accept", "text/event-stream")
                } else {
                    addHeader("Accept", "application/json")
                }
                if (apiKey.isNotBlank()) {
                    addHeader("Authorization", "Bearer $apiKey")
                }
            }
            .build()
    }

    fun resolveChatUrl(baseUrl: String): String {
        val normalized = baseUrl.trim().trimEnd('/')
        require(normalized.isNotBlank()) { "API 地址不能为空。" }
        require(normalized.startsWith("http://") || normalized.startsWith("https://")) {
            "API 地址需要以 http:// 或 https:// 开头。"
        }
        return if (normalized.endsWith("/chat/completions")) {
            normalized
        } else {
            "$normalized/chat/completions"
        }
    }

    /** 把 [ReasoningRequestFields.topLevel] 里的嵌套 Map/List 递归转为 org.json 类型；null 跳过。 */
    fun toJsonValue(value: Any?): Any? = when (value) {
        null -> null
        is Map<*, *> -> JSONObject().apply {
            value.forEach { (key, child) ->
                if (key != null) put(key.toString(), toJsonValue(child) ?: JSONObject.NULL)
            }
        }

        is List<*> -> JSONArray().apply {
            value.forEach {
                put(
                    toJsonValue(it) ?: JSONObject.NULL
                )
            }
        }

        else -> value
    }

    internal fun parseErrorMessage(responseText: String): String {
        val trimmedResponse = responseText.trim()
        if (trimmedResponse.isBlank()) {
            return "接口请求失败，服务器返回了空错误响应。"
        }

        extractReadableErrorMessage(trimmedResponse)
            ?.let { return truncateForError(it) }

        // HTML 包装的 JSON 错误（常见于网关/反代把上游 JSON 塞进页面）。
        extractJsonCandidate(trimmedResponse)?.let { jsonCandidate ->
            extractReadableErrorMessage(jsonCandidate)?.let { return truncateForError(it) }
        }

        val plainText = if (looksLikeHtml(trimmedResponse)) {
            htmlToPlainText(trimmedResponse)
        } else {
            trimmedResponse
        }.ifBlank { trimmedResponse }

        extractReadableErrorMessage(plainText)
            ?.let { return truncateForError(it) }
        extractJsonCandidate(plainText)?.let { jsonCandidate ->
            extractReadableErrorMessage(jsonCandidate)?.let { return truncateForError(it) }
        }

        val prefix = when {
            looksLikeHtml(trimmedResponse) ->
                "接口请求失败，服务器返回了 HTML 页面"

            looksLikeJson(trimmedResponse) ->
                "接口请求失败"

            else ->
                "接口请求失败，服务器返回了非 JSON 错误响应"
        }
        return truncateForError("$prefix：${plainText}")
    }

    private fun extractJsonCandidate(text: String): String? {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return null
        }
        if (looksLikeJson(trimmed)) {
            return trimmed
        }

        // 优先抓 <pre>/<code> 里的 JSON。
        val preMatch = Regex(
            pattern = """(?is)<(?:pre|code)[^>]*>\s*(\{.*?\}|\[.*?])\s*</(?:pre|code)>"""
        ).find(trimmed)?.groupValues?.getOrNull(1)
        if (!preMatch.isNullOrBlank()) {
            return preMatch.trim()
        }

        val objectStart = trimmed.indexOf('{')
        val arrayStart = trimmed.indexOf('[')
        val start = when {
            objectStart < 0 -> arrayStart
            arrayStart < 0 -> objectStart
            else -> minOf(objectStart, arrayStart)
        }
        if (start < 0) {
            return null
        }

        val candidate = trimmed.substring(start).trim()
        // 逐步回退到最后一个 } 或 ]，尽量恢复被 HTML 包裹的 JSON。
        for (end in candidate.length downTo 2) {
            val ch = candidate[end - 1]
            if (ch != '}' && ch != ']') {
                continue
            }
            val slice = candidate.substring(0, end).trim()
            if (runCatching { JSONObject(slice) }.isSuccess ||
                runCatching { JSONArray(slice) }.isSuccess
            ) {
                return slice
            }
        }
        return null
    }

    private fun extractReadableErrorMessage(responseText: String): String? {
        val root = runCatching { JSONObject(responseText.trim()) }.getOrNull() ?: return null
        val preferred = mutableListOf<String>()
        val fallbacks = mutableListOf<String>()
        val nestedError = root.opt("error")
        when (nestedError) {
            is JSONObject -> {
                extractReadableErrorFields(nestedError)?.let { message ->
                    val type = nestedError.optString("type").takeIf { it.isNotBlank() }
                    preferred += if (type != null && !message.contains(type, ignoreCase = true)) {
                        "$message（$type）"
                    } else {
                        message
                    }
                }
                val compact = nestedError.toString()
                if (compact.isNotBlank() && compact != "{}") {
                    fallbacks += compact
                }
            }

            is String -> if (nestedError.isNotBlank()) {
                preferred += nestedError
            }

            is Number, is Boolean -> preferred += nestedError.toString()
        }

        extractReadableErrorFields(root)?.let { preferred += it }

        // 优先返回信息量更大的可读错误字段，避免只拿到 "Service Unavailable"。
        preferred
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .maxByOrNull { it.length }
            ?.let { return it }

        return fallbacks
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .maxByOrNull { it.length }
    }


    private fun extractReadableErrorFields(root: JSONObject): String? {
        val preferredKeys = listOf(
            "message",
            "msg",
            "detail",
            "error_description",
            "error_msg",
            "errorMessage",
            "reason",
            "description",
            "error"
        )
        for (key in preferredKeys) {
            when (val value = root.opt(key)) {
                is String -> if (value.isNotBlank()) return value
                is Number, is Boolean -> return value.toString()
                is JSONObject -> extractReadableErrorFields(value)?.let { return it }
                is JSONArray -> {
                    val joined = buildString {
                        for (index in 0 until value.length()) {
                            val item = value.opt(index) ?: continue
                            val text = when (item) {
                                is String -> item
                                is JSONObject -> extractReadableErrorFields(item) ?: item.toString()
                                else -> item.toString()
                            }.trim()
                            if (text.isBlank()) continue
                            if (isNotEmpty()) append("; ")
                            append(text)
                        }
                    }
                    if (joined.isNotBlank()) return joined
                }
            }
        }
        return null
    }

    fun truncateForError(text: String): String {
        val compact = text.replace(Regex("\\s+"), " ").trim()
        return if (compact.length <= ERROR_SNIPPET_MAX_LENGTH) {
            compact
        } else {
            compact.take(ERROR_SNIPPET_MAX_LENGTH) + "..."
        }
    }


    private fun htmlToPlainText(html: String): String {
        return html
            .replace(Regex("(?is)<(script|style|noscript)[^>]*>.*?</\\1>"), " ")
            .replace(Regex("(?i)<br\\s*/?>"), "\n")
            .replace(
                Regex("(?i)</(p|div|section|article|header|footer|main|li|tr|h[1-6]|pre)>"),
                "\n"
            )
            .replace(Regex("<[^>]+>"), " ")
            .replace(Regex("&nbsp;"), " ")
            .replace(Regex("&lt;"), "<")
            .replace(Regex("&gt;"), ">")
            .replace(Regex("&quot;"), "\"")
            .replace(Regex("&#39;"), "'")
            .replace(Regex("&amp;"), "&")
            .replace(Regex("[ \\t\\x0B\\f\\r]+"), " ")
            .replace(Regex(" *\\n *"), "\n")
            .replace(Regex("\\n{2,}"), "\n")
            .trim()
    }

    fun safeContentType(contentType: String): String {
        return contentType.substringBefore(';').trim().ifBlank { "未返回" }
    }

    fun looksLikeErrorPayload(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return false
        }
        val root = runCatching { JSONObject(trimmed) }.getOrNull()
            ?: extractJsonCandidate(trimmed)?.let { candidate ->
                runCatching { JSONObject(candidate) }.getOrNull()
            }
            ?: return false
        if (root.has("error")) {
            return true
        }
        val message = root.optString("message")
        val type = root.optString("type")
        return message.isNotBlank() && (
                type.equals("api_error", ignoreCase = true) ||
                        type.equals("error", ignoreCase = true) ||
                        root.has("code") ||
                        root.has("status")
                )
    }

    fun JSONArray?.isNullOrEmpty(): Boolean {
        return this == null || length() == 0
    }

    fun looksLikeJson(text: String): Boolean {
        val normalized = text.trimStart()
        return normalized.startsWith("{") || normalized.startsWith("[")
    }

    fun looksLikeHtml(text: String): Boolean {
        val normalized = text.trimStart().lowercase()
        return normalized.startsWith("<!doctype") ||
                normalized.startsWith("<html") ||
                normalized.startsWith("<head") ||
                normalized.startsWith("<body") ||
                normalized.startsWith("<title") ||
                normalized.startsWith("<pre") ||
                (
                        normalized.contains("<html") &&
                                (normalized.contains("<body") || normalized.contains("<head"))
                        )
    }

    fun JSONObject.optTokenInt(name: String): Int? {
        if (!has(name) || isNull(name)) {
            return null
        }
        val number = when (val value = opt(name)) {
            is Number -> value.toLong()
            is String -> value.toLongOrNull()
            else -> null
        } ?: return null
        return number.takeIf { it in 0..Int.MAX_VALUE.toLong() }?.toInt()
    }

    fun buildEmptyResponseMessage(finishReason: String?): String {
        return when (finishReason) {
            "length" -> "接口返回内容为空，模型输出因达到长度限制被截断。"
            "content_filter" -> "接口返回内容为空，模型输出被内容安全策略过滤。"
            null, "stop", "tool_calls" -> "接口返回内容为空。"
            else -> "接口返回内容为空，结束原因：${
                truncateForError(
                    finishReason
                )
            }。"
        }
    }

    fun buildFinishReasonNotice(finishReason: String?, hasToolCalls: Boolean): String {
        if (finishReason == null || finishReason == "stop" || finishReason == "tool_calls" || hasToolCalls) {
            return ""
        }
        return when (finishReason) {
            "length" -> "\n\n> 提示：模型输出因达到长度限制被截断。"
            "content_filter" -> "\n\n> 提示：模型输出被内容安全策略过滤，部分内容可能缺失。"
            else -> "\n\n> 提示：模型以非标准原因结束：${
                truncateForError(
                    finishReason
                )
            }。"
        }
    }

    override fun log(m: String) {
        ALog.e(Component, m)
    }
}