package io.ai.chat.clientimpl

import io.ai.chat.client.OnAiChatListener
import io.ai.chat.client.SendPayload
import io.ai.chat.model.AIChatMessage
import io.ai.chat.model.AiToolCall
import io.ai.chat.model.ApiSettings
import io.ai.chat.model.ChatAttachment
import io.ai.chat.model.ChatAttachmentType
import io.ai.chat.model.ChatCompletionResult
import io.ai.chat.model.ChatRole
import io.ai.chat.model.ReasoningEffortOptions
import io.ai.chat.model.ResolvedModel
import io.ai.chat.model.TokenUsage
import io.ai.chat.model.ToolCallAccumulator
import io.ai.chat.tools.LocalAiTools
import io.ai.chat.utils.ALog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject


/**
 * by DAD FZ
 * 2026/8/21
 * desc：
 **/
class DeepSeekTestClientImpl : ClientImpl(),
    CoroutineScope by CoroutineScope(Dispatchers.Main + SupervisorJob()) {

    private var listener: OnAiChatListener? = null

    private val url = "https://api.deepseek.com"
    private val aiKey = "sk-9d714e4f3a99457c9c352c17be1e431a"
    private val requestUrl = "$url/chat/completions"


    override fun registerAiChatListener(listener: OnAiChatListener) {
        this.listener = listener
    }

    override fun sendMessage(payload: SendPayload) {
        launch {
//            testSendAI()
        }
    }


    private suspend fun streamChatOnce(
        model: ResolvedModel,
        settings: ApiSettings,
        messages: List<AIChatMessage>,
        includeUsage: Boolean,
        onDelta: suspend (String) -> Unit,
        onThinkingDelta: suspend (String) -> Unit
    ): ChatCompletionResult {
        return withContext(Dispatchers.IO) {
            require(model.baseUrl.isNotBlank()) { "API 地址不能为空。" }
            require(model.model.isNotBlank()) { "模型不能为空。" }
            val startMs = System.currentTimeMillis()

            val toolDefinitions = LocalAiTools.toolDefinitions(settings)

            log(
                ALog.event(
                    "开始流式聊天请求",
                    "baseUrl" to ALog.safeUrlOrigin(model.baseUrl),
                    "messages" to messages.size,
                    "tools" to toolDefinitions.length(),
                    "includeUsage" to includeUsage,
                    "reasoningEffortConfigured" to model.reasoningEffort.isNotBlank(),
                    "providerType" to model.providerType.storageKey,
                    "modelLength" to model.model.length
                )
            )

            val requestBody = JSONObject().apply {
                put("model", model.model)
                put("stream", true)
                if (includeUsage) {
                    put(
                        "stream_options",
                        JSONObject().apply {
                            put("include_usage", true)
                        }
                    )
                }
                put("tools", toolDefinitions)

                putReasoningFields(model)
                put(
                    "messages",
                    JSONArray().apply {
                        messages.forEach { message ->
                            put(buildApiMessage(message))
                        }
                    }
                )
            }.toString()

            ALog.e(Component, "请求body:$requestBody")

            val request = buildRequest(
                model.baseUrl,
                model.apiKey,
                requestBody,
                isStreaming = true
            )
            val response = executeRequest(request)

            val statusCode = response.code
            log(
                ALog.event(
                    "聊天接口已响应",
                    "status" to statusCode,
                    "durationMs" to ALog.durationMs(startMs)
                )
            )
            if (!response.isSuccessful) {
                log(ALog.event("聊天接口返回失败状态", "status" to statusCode))
                val responseText = response.body?.string() ?: ""
                val errorMessage = parseErrorMessage(responseText)
                throw IllegalStateException(errorMessage.ifBlank { "接口请求失败，HTTP $statusCode" })
            }

            val contentType = response.header("Content-Type").orEmpty()

            log(
                ALog.event(
                    "聊天响应内容类型",
                    "contentType" to safeContentType(contentType)
                )
            )

            if (contentType.contains("text/event-stream", ignoreCase = true)) {
                readStreamedReply(
                    response,
                    onDelta,
                    onThinkingDelta
                ).also { result ->
                    log(
                        ALog.event(
                            "流式聊天请求完成",
                            "finishReason" to (result.finishReason ?: "未返回"),
                            "contentLength" to result.content.length,
                            "thinkingLength" to result.thinkingContent.length,
                            "toolCalls" to result.toolCalls.size,
                            "durationMs" to ALog.durationMs(startMs),
                            *result.usage.safeLogFields()
                        )
                    )
                }
            } else {
                log(
                    ALog.event(
                        "聊天接口返回非 SSE 响应，使用非流式解析",
                        "contentType" to safeContentType(
                            contentType
                        )
                    )
                )
                val responseText = response.body?.string() ?: ""
                currentCoroutineContext().ensureActive()
                // 部分网关/反代会以 200 + HTML/错误 JSON 返回失败，先当错误响应解析。
                if (looksLikeHtml(responseText) || looksLikeErrorPayload(responseText)) {
                    val errorMessage = parseErrorMessage(responseText)
                    throw IllegalStateException(errorMessage.ifBlank { "接口请求失败，HTTP $statusCode" })
                }
                val result = parseAssistantReply(responseText)
                withContext(mainDispatcher) {
                    if (result.content.isNotBlank()) {
                        onDelta(result.content)
                    }
                }
                result.also {
                    log(
                        ALog.event(
                            "非流式聊天请求完成",
                            "finishReason" to (it.finishReason ?: "未返回"),
                            "contentLength" to it.content.length,
                            "thinkingLength" to it.thinkingContent.length,
                            "toolCalls" to it.toolCalls.size,
                            "durationMs" to ALog.durationMs(startMs),
                            *it.usage.safeLogFields()
                        )
                    )
                }
            }
        }
    }

    private fun parseAssistantReply(
        responseText: String,
        appendFinishReasonNotice: Boolean = true
    ): ChatCompletionResult {
        val trimmedResponse = responseText.trim()
        val responseJson = runCatching { JSONObject(trimmedResponse) }.getOrElse { error ->
            throw IllegalStateException(
                parseErrorMessage(
                    trimmedResponse
                ), error
            )
        }
        responseJson.opt("error")?.let { errorNode ->
            val errorText = when (errorNode) {
                is JSONObject -> errorNode.toString()
                is String -> errorNode
                else -> errorNode.toString()
            }
            // 有些网关会在 200 响应里塞 error，同时不给 choices。
            if (responseJson.optJSONArray("choices").isNullOrEmpty()) {
                throw IllegalStateException(
                    parseErrorMessage(
                        errorText.ifBlank { trimmedResponse })
                )
            }
        }
        val choices = responseJson.optJSONArray("choices")
            ?: throw IllegalStateException("接口返回缺少 choices 字段。")
        if (choices.length() == 0) {
            throw IllegalStateException("接口没有返回任何候选结果。")
        }

        val firstChoice = choices.getJSONObject(0)
        val messageObject = firstChoice.optJSONObject("message")
        val toolCalls = parseToolCalls(messageObject?.optJSONArray("tool_calls"))
        val content = when {
            messageObject != null -> extractMessageContent(
                messageObject.opt("content")
            )

            firstChoice.has("text") -> firstChoice.optString("text")
            else -> ""
        }

        val thinkingContent =
            messageObject?.let {
                extractMessageContent(
                    it.opt(
                        "reasoning_content"
                    )
                )
            }.orEmpty()
        val finishReason =
            firstChoice.optString("finish_reason").takeIf { it.isNotBlank() && it != "null" }
        val usage = parseTokenUsage(responseJson.optJSONObject("usage"))
        if (content.isBlank() && thinkingContent.isBlank() && toolCalls.isEmpty()) {
            throw IllegalStateException(buildEmptyResponseMessage(finishReason))
        }

        val finishReasonNotice = if (appendFinishReasonNotice) {
            buildFinishReasonNotice(finishReason, hasToolCalls = toolCalls.isNotEmpty())
        } else {
            ""
        }
        val finalContent = if (finishReasonNotice.isNotEmpty() && toolCalls.isEmpty()) {
            content + finishReasonNotice
        } else {
            content
        }

        return ChatCompletionResult(
            content = finalContent,
            thinkingContent = thinkingContent,
            toolCalls = toolCalls,
            finishReason = finishReason,
            usage = usage
        )
    }

    private fun parseToolCalls(toolCallsArray: JSONArray?): List<AiToolCall> {
        if (toolCallsArray == null) {
            return emptyList()
        }

        return buildList {
            for (index in 0 until toolCallsArray.length()) {
                val item = toolCallsArray.optJSONObject(index) ?: continue
                val functionObject = item.optJSONObject("function") ?: continue
                val id = item.optString("id")
                val name = functionObject.optString("name")
                val arguments = functionObject.optString("arguments")
                if (id.isBlank() || name.isBlank()) {
                    continue
                }
                add(
                    AiToolCall(
                        id = id,
                        name = name,
                        arguments = arguments
                    )
                )
            }
        }
    }

    private suspend fun readStreamedReply(
        response: Response,
        onDelta: suspend (String) -> Unit,
        onThinkingDelta: suspend (String) -> Unit
    ): ChatCompletionResult {
        val coroutineContext = currentCoroutineContext()
        val replyBuilder = StringBuilder()
        val thinkingBuilder = StringBuilder()
        var streamFinished = false
        var finishReason: String? = null
        val toolCalls = linkedMapOf<Int, ToolCallAccumulator>()
        var usage: TokenUsage? = null

        response.body?.byteStream()?.bufferedReader(Charsets.UTF_8)?.use { reader ->
            val eventPayload = StringBuilder()
            while (true) {
                coroutineContext.ensureActive()
                val line = reader.readLine() ?: break
                if (line.isBlank()) {
                    if (eventPayload.isNotEmpty()) {
                        coroutineContext.ensureActive()
                        streamFinished = processStreamEvent(
                            payload = eventPayload.toString(),
                            replyBuilder = replyBuilder,
                            thinkingBuilder = thinkingBuilder,
                            toolCalls = toolCalls,
                            onFinishReason = { finishReason = it },
                            onUsage = { usage = it },
                            onDelta = onDelta,
                            onThinkingDelta = onThinkingDelta
                        )
                        eventPayload.clear()
                        if (streamFinished) {
                            break
                        }
                    }
                    continue
                }

                if (line.startsWith("data:")) {
                    if (eventPayload.isNotEmpty()) {
                        eventPayload.append('\n')
                    }
                    eventPayload.append(line.substringAfter("data:").trimStart())
                }
            }

            if (!streamFinished && eventPayload.isNotEmpty()) {
                coroutineContext.ensureActive()
                processStreamEvent(
                    payload = eventPayload.toString(),
                    replyBuilder = replyBuilder,
                    thinkingBuilder = thinkingBuilder,
                    toolCalls = toolCalls,
                    onFinishReason = { finishReason = it },
                    onUsage = { usage = it },
                    onDelta = onDelta,
                    onThinkingDelta = onThinkingDelta
                )
            }
        }

        val content = replyBuilder.toString()
        val thinkingContent = thinkingBuilder.toString()
        val finalizedToolCalls = toolCalls
            .toSortedMap()
            .values
            .mapNotNull { accumulator ->
                val id = accumulator.id.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val name = accumulator.name.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                AiToolCall(
                    id = id,
                    name = name,
                    arguments = accumulator.arguments.toString()
                )
            }

        if (content.isBlank() && thinkingContent.isBlank() && finalizedToolCalls.isEmpty()) {
            throw IllegalStateException(buildEmptyResponseMessage(finishReason))
        }

        val finishReasonNotice =
            buildFinishReasonNotice(finishReason, hasToolCalls = finalizedToolCalls.isNotEmpty())
        if (finishReasonNotice.isNotEmpty() && finalizedToolCalls.isEmpty()) {
            withContext(mainDispatcher) {
                onDelta(finishReasonNotice)
            }
            replyBuilder.append(finishReasonNotice)
        }

        return ChatCompletionResult(
            content = replyBuilder.toString(),
            thinkingContent = thinkingContent,
            toolCalls = finalizedToolCalls,
            finishReason = finishReason,
            usage = usage
        )
    }

    private suspend fun processStreamEvent(
        payload: String,
        replyBuilder: StringBuilder,
        thinkingBuilder: StringBuilder,
        toolCalls: MutableMap<Int, ToolCallAccumulator>,
        onFinishReason: (String) -> Unit,
        onUsage: (TokenUsage) -> Unit,
        onDelta: suspend (String) -> Unit,
        onThinkingDelta: suspend (String) -> Unit
    ): Boolean {
        currentCoroutineContext().ensureActive()
        val normalizedPayload = payload.trim()
        if (normalizedPayload.isEmpty()) {
            return false
        }
        if (normalizedPayload == "[DONE]") {
            return true
        }

        val responseJson = parseStreamPayload(normalizedPayload)
        responseJson.opt("error")?.let { errorNode ->
            val errorText = when (errorNode) {
                is JSONObject -> errorNode.toString()
                is String -> errorNode
                else -> errorNode.toString()
            }
            throw IllegalStateException(
                parseErrorMessage(
                    errorText.ifBlank { normalizedPayload })
            )
        }
        parseTokenUsage(responseJson.optJSONObject("usage"))?.let(onUsage)
        val choices = responseJson.optJSONArray("choices") ?: return false
        val firstChoice = choices.optJSONObject(0) ?: return false
        val finishReason =
            firstChoice.optString("finish_reason").takeIf { it.isNotBlank() && it != "null" }
        if (finishReason != null) {
            onFinishReason(finishReason)
        }
        val deltaObject = firstChoice.optJSONObject("delta")
        val toolCallsArray = deltaObject?.optJSONArray("tool_calls")
        if (toolCallsArray != null) {
            appendToolCallDeltas(toolCallsArray, toolCalls)
        }

        val thinkingDelta =
            deltaObject?.let { extractMessageContent(it.opt("reasoning_content")) }.orEmpty()
        if (thinkingDelta.isNotEmpty()) {
            thinkingBuilder.append(thinkingDelta)
            currentCoroutineContext().ensureActive()
            onThinkingDelta(thinkingDelta)
        }

        val deltaText = when {
            deltaObject != null -> extractMessageContent(deltaObject.opt("content"))
            firstChoice.has("text") -> firstChoice.optString("text")
            else -> ""
        }
        if (deltaText.isNotEmpty()) {
            replyBuilder.append(deltaText)
            currentCoroutineContext().ensureActive()
            onDelta(deltaText)
        }
        return false
    }

    private fun extractMessageContent(content: Any?): String {
        return when (content) {
            is String -> content
            is JSONArray -> buildString {
                for (index in 0 until content.length()) {
                    when (val item = content.opt(index)) {
                        is String -> append(item)
                        is JSONObject -> {
                            val text = item.optString("text")
                            if (text.isNotBlank()) {
                                append(text)
                            }
                        }
                    }
                }
            }

            else -> ""
        }
    }

    private fun appendToolCallDeltas(
        toolCallsArray: JSONArray,
        accumulators: MutableMap<Int, ToolCallAccumulator>
    ) {
        for (index in 0 until toolCallsArray.length()) {
            val item = toolCallsArray.optJSONObject(index) ?: continue
            val itemIndex = item.optInt("index", index)
            val accumulator = accumulators.getOrPut(itemIndex) { ToolCallAccumulator() }

            val id = item.optString("id")
            if (id.isNotBlank()) {
                accumulator.id = id
            }

            val functionObject = item.optJSONObject("function")
            val name = functionObject?.optString("name").orEmpty()
            if (name.isNotBlank()) {
                accumulator.name = name
            }

            val argumentsDelta = functionObject?.optString("arguments").orEmpty()
            if (argumentsDelta.isNotEmpty()) {
                accumulator.arguments.append(argumentsDelta)
            }
        }
    }

    internal fun parseTokenUsage(usageObject: JSONObject?): TokenUsage? {
        usageObject ?: return null
        val inputTokens = usageObject.optTokenInt("input_tokens")
            ?: usageObject.optTokenInt("prompt_tokens")
        val outputTokens = usageObject.optTokenInt("output_tokens")
            ?: usageObject.optTokenInt("completion_tokens")
        val totalTokens = usageObject.optTokenInt("total_tokens")
        val inputDetails = usageObject.optJSONObject("input_tokens_details")
            ?: usageObject.optJSONObject("prompt_tokens_details")
        val outputDetails = usageObject.optJSONObject("output_tokens_details")
            ?: usageObject.optJSONObject("completion_tokens_details")

        val cachedInputTokens = inputDetails?.optTokenInt("cached_tokens")
        val reasoningOutputTokens = outputDetails?.optTokenInt("reasoning_tokens")
        if (inputTokens == null &&
            outputTokens == null &&
            totalTokens == null &&
            cachedInputTokens == null &&
            reasoningOutputTokens == null
        ) {
            return null
        }

        return TokenUsage(
            inputTokens = inputTokens,
            outputTokens = outputTokens,
            totalTokens = totalTokens,
            cachedInputTokens = cachedInputTokens,
            reasoningOutputTokens = reasoningOutputTokens
        )
    }

    private fun parseStreamPayload(payload: String): JSONObject {
        return runCatching { JSONObject(payload) }.getOrElse { error ->
            if (looksLikeHtml(payload) || looksLikeErrorPayload(
                    payload
                )
            ) {
                throw IllegalStateException(
                    parseErrorMessage(
                        payload
                    ), error
                )
            }
            throw IllegalStateException(
                "流式响应解析失败，payload 片段：${
                    truncateForError(
                        payload
                    )
                }",
                error
            )
        }
    }

    internal fun buildApiMessage(message: AIChatMessage): JSONObject {
        return JSONObject().apply {
            put("role", message.role.apiValue)
            when (message.role) {
                ChatRole.Tool -> {
                    put("content", message.content)
                    put("tool_call_id", message.toolCallId)
                }

                ChatRole.Assistant -> {
                    put("content", message.content.ifEmpty { JSONObject.NULL })
                    if (message.toolCalls.isNotEmpty()) {
                        put(
                            "tool_calls",
                            JSONArray().apply {
                                message.toolCalls.forEach { toolCall ->
                                    put(
                                        JSONObject().apply {
                                            put("id", toolCall.id)
                                            put("type", "function")
                                            put(
                                                "function",
                                                JSONObject().apply {
                                                    put("name", toolCall.name)
                                                    put("arguments", toolCall.arguments)
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    }
                }

                ChatRole.User -> {
                    put("content", buildUserApiContent(message))
                }

                else -> {
                    put("content", message.content)
                }
            }
        }
    }

    private fun buildUserApiContent(message: AIChatMessage): Any {
        if (message.attachments.isEmpty()) {
            return message.content
        }

        val textContent = buildUserTextContent(message)
        val imageAttachments = message.attachments.filter { attachment ->
            attachment.type == ChatAttachmentType.Image && !attachment.base64Data.isNullOrBlank()
        }
        if (imageAttachments.isEmpty()) {
            return textContent
        }

        return JSONArray().apply {
            if (textContent.isNotBlank()) {
                put(
                    JSONObject().apply {
                        put("type", "text")
                        put("text", textContent)
                    }
                )
            }
            imageAttachments.forEach { attachment ->
                put(
                    JSONObject().apply {
                        put("type", "image_url")
                        put(
                            "image_url",
                            JSONObject().apply {
                                put("url", attachment.toDataUrl())
                            }
                        )
                    }
                )
            }
        }
    }

    private fun ChatAttachment.toDataUrl(): String {
        val resolvedMimeType = mimeType
            ?.takeIf { it.startsWith("image/", ignoreCase = true) }
            ?: "image/jpeg"
        return "data:$resolvedMimeType;base64,$base64Data"
    }

    private fun buildUserTextContent(message: AIChatMessage): String {
        if (message.attachments.isEmpty()) {
            return message.content
        }

        return buildString {
            if (message.content.isNotBlank()) {
                append(message.content)
                append("\n\n")
            }
            append("【附件】")
            message.attachments.forEachIndexed { index, attachment ->
                append('\n')
                append(index + 1)
                append(". ")
                append(
                    when (attachment.type) {
                        ChatAttachmentType.Image -> "图片"
                        ChatAttachmentType.File -> "文件"
                    }
                )
                append("：")
                append(attachment.displayName.ifBlank { attachment.path })
                attachment.mimeType?.takeIf { it.isNotBlank() }?.let { mimeType ->
                    append("\n   MIME：")
                    append(mimeType)
                }
                attachment.path.takeIf { it.isNotBlank() }?.let { path ->
                    append("\n   路径：")
                    append(path)
                }
                when (attachment.type) {
                    ChatAttachmentType.Image -> {
                        append("\n   说明：图片已作为 base64 data URL 随本消息发送。")
                    }

                    ChatAttachmentType.File -> appendFileAttachmentContent(attachment)
                }
            }
        }
    }

    private fun StringBuilder.appendFileAttachmentContent(attachment: ChatAttachment) {
        if (attachment.directReadable) {
            append("\n   说明：应用可直接读取该路径，请按需调用本地文件工具读取。")
            return
        }

        val textContent = attachment.textContent
        if (textContent == null) {
            append("\n   说明：应用无法直接读取该路径，也未能读取文件内容。")
            return
        }

        append("\n   说明：应用无法直接读取该路径，已通过 ContentResolver 读取文本内容。")
        if (attachment.truncated) {
            append("内容过长，以下内容已截断。")
        }
        append("\n   内容：\n")
        append(textContent.ifBlank { "（文件内容为空）" })
    }

    private suspend fun testSendAI() {
        withContext(Dispatchers.IO) {
            val requestBody = JSONObject(
                """
                {
                    "model": "deepseek-v4-flash",
                    "stream": true,
                    "stream_options": {
                        "include_usage": true
                    },
                    "reasoning_effort": "high",
                    "thinking": {
                        "type": "enabled"
                    },
                    "messages": [
                        {
                            "role": "system",
                            "content": "# 角色定义\n你是一个 Android 系统的本地 AI Agent，负责协助用户完成设备级任务。你可以调用工具来读取本地文件和执行 Shell 命令。\n\n# 环境规范\n- 基础环境：Android busybox（执行器已自动注入相关环境变量）。\n- 路径约定：优先操作 `\/sdcard\/` 或应用私有目录。访问其他目录时需注意 Android 沙盒机制与读写权限限制。\n- 附件约定：用户消息可能附带图片或文件；图片会以 base64 图片内容随消息提供，文件在可直接读取时只提供路径，否则会附带通过 ContentResolver 读取到的文本内容。\n\n# 安全协议（最高优先级）\n- 拦截机制：当你调用高风险指令（如 `rm` 删除、`mv` 覆盖、修改敏感配置等）时，宿主应用会自动拦截并弹窗要求用户确认。\n- 执行准则：执行高风险命令之前必须先向用户说明风险，用户同意之后才能继续执行。"
                        },
                        {
                            "role": "user",
                            "content": "Java 版 Android 倒计时"
                        }
                    ]
                }
            """.trimIndent()
            ).toString()

            val request = buildRequest(
                url,
                aiKey,
                requestBody,
                isStreaming = true
            )
            val response = executeRequest(request)

            val statusCode = response.code
            log("聊天接口已响应" + ",status:" + statusCode)
            if (!response.isSuccessful) {
                val responseText = response.body?.string() ?: ""
//                val errorMessage = parseErrorMessage(responseText)
                log("聊天接口返回失败状态" + "status:" + statusCode + ",responseText:" + responseText)
                return@withContext
            }

            val contentType = response.header("Content-Type").orEmpty()
            log(
                "聊天响应内容类型" + "contentType:" + safeContentType(
                    contentType
                )
            )
            if (contentType.contains("text/event-stream", ignoreCase = true)) {
                readStreamedReply(response, {}, {}).also { result ->
//                    MoteLog.i(
//                        Component,
//                        MoteLog.event(
//                            "流式聊天请求完成",
//                            "finishReason" to (result.finishReason ?: "未返回"),
//                            "contentLength" to result.content.length,
//                            "thinkingLength" to result.thinkingContent.length,
//                            "toolCalls" to result.toolCalls.size,
//                            "durationMs" to MoteLog.durationMs(startMs),
//                            *result.usage.safeLogFields()
//                        )
//                    )
                }
            }
        }
    }

    private fun JSONObject.putReasoningFields(
        model: ResolvedModel,
        skipKeys: Set<String> = emptySet()
    ) {
        val reasoningFields =
            ReasoningEffortOptions.encode(model.providerType, model.reasoningEffort)
        if ("reasoning_effort" !in skipKeys) {
            reasoningFields.reasoningEffort?.let { put("reasoning_effort", it) }
        }
        reasoningFields.topLevel.forEach { (key, value) ->
            if (key !in skipKeys) {
                toJsonValue(value)?.let { put(key, it) }
            }
        }
    }

    private fun TokenUsage?.safeLogFields(): Array<Pair<String, Any?>> {
        return arrayOf(
            "inputTokens" to this?.inputTokens,
            "outputTokens" to this?.outputTokens,
            "totalTokens" to this?.totalTokens,
            "cachedInputTokens" to this?.cachedInputTokens,
            "reasoningOutputTokens" to this?.reasoningOutputTokens
        )
    }

    override fun onDestroy() {
        this.listener = null
        cancel()
    }
}