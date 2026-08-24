package io.ai.chat.config

import io.ai.chat.client.AIClient
import io.ai.chat.clientimpl.DeepSeekTestClientImpl


/**
 * by DAD FZ
 * 2026/8/21
 * desc：
 **/
class AiOptions {

    var client: AIClient? = DeepSeekTestClientImpl()

}