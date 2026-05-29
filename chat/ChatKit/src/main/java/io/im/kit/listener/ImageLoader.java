package io.im.kit.listener;

import android.content.Context;
import android.widget.ImageView;

import io.im.lib.model.Message;
import io.im.lib.model.UserInfo;

/**
 * author : JFZ
 * date : 2024/1/27 17:14
 * description :
 */
public interface ImageLoader {

    void loadConversationAvatar(Context context, ImageView view, Message message, boolean isSender);

    void loadForwardSelectorAvatar(Context context, ImageView view, UserInfo userInfo);

}
