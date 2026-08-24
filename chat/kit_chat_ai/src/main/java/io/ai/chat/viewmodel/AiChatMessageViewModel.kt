package io.ai.chat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.ai.chat.client.AIClient
import io.ai.chat.client.OnAiChatListener
import io.ai.chat.client.SendPayload
import io.ai.chat.provider.InitAIChatProvider


/**
 * by DAD FZ
 * 2026/8/21
 * desc：
 **/
class AiChatMessageViewModel(application: Application) : AndroidViewModel(application),
    OnAiChatListener {

    private var client: AIClient? = null

    init {
        client = InitAIChatProvider.aiOptions.client
        client?.registerAiChatListener(this)
    }

    fun sendMessage() {
        client?.sendMessage(SendPayload())
    }


    fun onDestroy() {
        client?.onDestroy()
    }

}