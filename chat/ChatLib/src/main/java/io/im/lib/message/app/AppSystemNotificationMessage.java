package io.im.lib.message.app;

import androidx.annotation.Keep;

import org.json.JSONObject;

import io.im.lib.model.MessageContent;

/**
 * author : JFZ
 * date : 2024/1/27 11:46
 * description : 应用系统消息类型
 */
@Keep
public final class AppSystemNotificationMessage extends MessageContent {
    @Override
    public MessageContent parseContent(JSONObject obj) {
        return this;
    }


    public static AppSystemNotificationMessage obtain() {
        return new AppSystemNotificationMessage();
    }
}
