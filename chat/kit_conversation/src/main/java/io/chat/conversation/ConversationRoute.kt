package io.chat.conversation

import android.content.Context
import io.im.uicommon.route.IMRoute
import io.im.uicommon.route.RouterConstant


/**
 * by DAD FZ
 * 2026/6/11
 * desc：
 **/
object ConversationRoute {

    fun goConversation(context: Context) {
        IMRoute.withKey(RouterConstant.PAGE_CONVERSATION_PAGE)
            .withContext(context)
            .navigate()
    }
}