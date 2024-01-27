package io.im.kit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import io.im.kit.conversation.ConversationActivity;
import io.im.lib.model.UserInfo;

/**
 * author : JFZ
 * date : 2024/1/26 11:23
 * description :
 */
public class RouteUtil {

    public static final String User = "conversation_user";

    public static final String ConversationType = "conversation_type";

    public static final String InputStyle = "input_style";

    public static void goPrivateChat(Context context, UserInfo user) {
        go(context, new Intent(context, ConversationActivity.class)
                .putExtra(User, user)
                .putExtra(InputStyle, io.im.kit.config.InputStyle.All.getType())
                .putExtra(ConversationType, io.im.lib.model.ConversationType.PRIVATE.getValue()));
    }

    private static void go(Context context, Intent intent) {
        if (context instanceof Activity) {
            context.startActivity(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
