package io.chat.conversation;


import android.content.Context;
import android.content.Intent;

import io.chat.conversation.ui.ConversationActivity;
import io.im.uicommon.helper.RouteHelper;

/**
 * by DAD FZ
 * 2026/6/10
 * desc：
 **/
public class ConversationRoute {

    public static void goConversation(Context context) {
        RouteHelper.go(context, new Intent(context, ConversationActivity.class));
    }
}
