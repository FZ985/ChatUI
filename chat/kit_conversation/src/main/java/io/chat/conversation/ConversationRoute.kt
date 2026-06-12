package io.chat.conversation

import android.content.Context
import android.content.Intent
import io.chat.conversation.ui.ConversationActivity
import io.im.uicommon.helper.RouteHelper


/**
 * by DAD FZ
 * 2026/6/11
 * desc：
 **/
object ConversationRoute {

    fun goConversation(context: Context) {
        RouteHelper.go(context, Intent(context, ConversationActivity::class.java))
    }
}