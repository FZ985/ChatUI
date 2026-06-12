package io.im.uicommon.listener;

import android.content.Context;

import io.im.core.model.Message;
import io.im.core.model.Session;
import io.im.core.model.UserInfo;
import io.im.uicommon.widgets.IAvatarView;

/**
 * author : JFZ
 * date : 2024/1/27 17:14
 * description :
 */
public interface ImageLoader {

    void loadChatAvatar(Context context, IAvatarView view, Message message, boolean isSender);

    void loadForwardSelectorAvatar(Context context, IAvatarView view, UserInfo userInfo);

    void loadConversationAvatar(Context context, IAvatarView view, Session session);

}
