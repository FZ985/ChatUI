package io.chat.kit;

import android.content.Context;
import android.content.Intent;

import io.im.core.model.UserInfo;
import io.chat.kit.chat.IChatActivity;
import io.chat.kit.forward.IForwardSelectorActivity;
import io.im.uicommon.helper.RouteHelper;

/**
 * author : JFZ
 * date : 2024/1/26 11:23
 * description :
 */
public class ChatRoute {

    public static final String User = "conversation_user";

    public static final String ConversationType = "conversation_type";

    public static final String InputStyle = "input_style";
    public static final String IsMerge = "is_merge";


    public static void goForwardSelect(Context context, UserInfo user, boolean merge) {
        RouteHelper.go(context, new Intent(context, IForwardSelectorActivity.class)
                .putExtra(User, user)
                .putExtra(IsMerge, merge));
    }

    public static void goPrivateChat(Context context, UserInfo user) {
        RouteHelper.go(context, new Intent(context, IChatActivity.class)
                .putExtra(User, user)
                .putExtra(InputStyle, io.chat.kit.config.enums.InputStyle.All.getType())
                .putExtra(ConversationType, io.im.core.model.ConversationType.TYPE_P2P.getValue()));
    }


}
