package io.im.kit.callback;

import android.content.Context;
import android.widget.ImageView;

import io.im.lib.model.Message;

/**
 * author : JFZ
 * date : 2024/1/27 17:14
 * description :
 */
public interface ImageLoader {

    void loadConversationAvatar(Context context, ImageView view, Message message,boolean isSender);

}
