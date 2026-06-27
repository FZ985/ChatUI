package io.chat.kit;

import android.content.Context;

import io.im.core.model.ConversationType;
import io.im.core.model.User;
import io.im.uicommon.route.IMRoute;
import io.im.uicommon.route.RouterConstant;

/**
 * author : JFZ
 * date : 2024/1/26 11:23
 * description :
 */
public class ChatRoute {


    public static void goForwardSelect(Context context, User user, boolean merge) {
        IMRoute.withKey(RouterConstant.PAGE_CHAT_FORWARD)
                .withContext(context)
                .withParam(RouterConstant.USER, user)
                .withParam(RouterConstant.IS_MERGE_SEND, merge)
                .navigate();
    }

    public static void goPrivateChat(Context context, User user) {
        IMRoute.withKey(RouterConstant.PAGE_CHAT_P2P)
                .withContext(context)
                .withParam(RouterConstant.USER, user)
                .withParam(RouterConstant.CONVERSATION_TYPE, ConversationType.TYPE_P2P.getValue())
                .navigate();
    }


}
