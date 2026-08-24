package io.ai.chat.client

import android.util.Log


/**
 * by DAD FZ
 * 2026/8/21
 * desc：
 **/
interface AIClient {

    fun registerAiChatListener(listener: OnAiChatListener)

    fun sendMessage(payload: SendPayload)


    fun onDestroy()

    fun log(m: String) {
        Log.e("AIClient", m)
    }
}


interface OnAiChatListener {}