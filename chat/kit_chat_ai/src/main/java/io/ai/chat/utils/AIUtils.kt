package io.ai.chat.utils

import org.json.JSONObject


/**
 * by DAD FZ
 * 2026/8/21
 * desc：
 **/

fun JSONObject.optIntOrNull(name: String): Int? {
    if (!has(name) || isNull(name)) {
        return null
    }
    return when (val value = opt(name)) {
        is Number -> value.toInt()
        is String -> value.toIntOrNull()
        else -> null
    }
}