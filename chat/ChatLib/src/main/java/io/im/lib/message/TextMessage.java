package io.im.lib.message;

import androidx.annotation.Keep;

import org.json.JSONObject;

import java.io.Serializable;

import io.im.lib.model.MessageContent;
import io.im.lib.utils.ChatNull;

/**
 * author : JFZ
 * date : 2024/1/27 13:31
 * description :
 */
@Keep
public final class TextMessage extends MessageContent implements Serializable {

    private String content;

    public static TextMessage obtain(String content) {
        TextMessage textMessage = new TextMessage();
        textMessage.setContent(content);
        return textMessage;
    }

    @Override
    public MessageContent parseContent(JSONObject obj) {
        if (obj != null) {
            setContent(obj.optString("content"));
        }
        return this;
    }

    public String getContent() {
        return ChatNull.compatValue(content);
    }

    public void setContent(String content) {
        this.content = content;
    }
}
