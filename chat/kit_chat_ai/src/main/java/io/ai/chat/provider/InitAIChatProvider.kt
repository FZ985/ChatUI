package io.ai.chat.provider

import android.app.Application
import android.util.Log
import io.ai.chat.config.AiOptions
import io.ai.chat.ui.AIChatActivity
import io.im.core.core.init.IKitInitializer
import io.im.core.core.init.InitializerManager
import io.im.core.core.init.InitializerProvider
import io.im.core.utils.JLog
import io.im.uicommon.route.IMRoute
import io.im.uicommon.route.RouterConstant
import io.ratex.RaTeXFontLoader


/**
 * by DAD FZ
 * 2026/8/20
 * desc：
 **/
class InitAIChatProvider : InitializerProvider(), IKitInitializer {

    companion object {
        const val KEY = "InitChatAIService"

        val aiOptions = AiOptions()
    }

    override fun onInitializer() {
        JLog.e("======InitAIChatProvider====init")
        InitializerManager.getInstance().registerInitializer(KEY, this)
    }

    override fun init(context: Application) {
        IMRoute.registerRouter(RouterConstant.PAGE_CHAT_AI_P2P, AIChatActivity::class.java)

        Thread(
            {
                Log.i("App", "开始后台初始化 RaTeX 字体")
                runCatching { RaTeXFontLoader.ensureLoaded(context) }
                    .onSuccess { count -> Log.i("App", "RaTeX 字体后台初始化结束：$count 个字体") }
                    .onFailure { error -> Log.w("App", "RaTeX 字体后台初始化异常", error) }
            },
            "RaTeXFontInit"
        ).start()

    }
}