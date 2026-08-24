package io.ai.chat.config

import android.content.Context
import io.im.uicommon.route.IMRoute
import io.im.uicommon.route.RouterConstant

/**
 * by DAD FZ
 * 2026/8/20
 * desc：
 **/
object AiRoute {


    fun goAI(context: Context) {
//        context.startActivity(Intent(context, AIChatActivity::class.java).apply {
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        })

        IMRoute.withKey(RouterConstant.PAGE_CHAT_AI_P2P)
            .withContext(context)
//            .withParam(RouterConstant.USER, user)
//            .withParam(RouterConstant.CONVERSATION_TYPE, ConversationType.TYPE_P2P.getValue())
            .navigate()
    }


}