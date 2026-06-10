package io.im.uicommon.listener;

import android.content.Context;
import android.widget.ImageView;

import io.im.core.model.Message;
import io.im.core.model.Session;
import io.im.core.model.UserInfo;

/**
 * author : JFZ
 * date : 2024/1/27 17:14
 * description :
 */
public interface ImageLoader {

    void loadConversationAvatar(Context context, ImageView view, Message message, boolean isSender);

    void loadForwardSelectorAvatar(Context context, ImageView view, UserInfo userInfo);

    void loadConversationAvatar(Context context, ImageView view, Session session);

}
