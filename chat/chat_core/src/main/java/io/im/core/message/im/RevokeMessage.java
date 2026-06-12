package io.im.core.message.im;

import android.content.Context;
import android.text.Spannable;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.Serializable;

import io.im.core.model.Message;
import io.im.core.model.MessageContent;
import io.im.core.utils.ChatNull;

/**
 * author : JFZ
 * date : 2024/1/27 13:31
 * description :
 */
@Keep
public final class RevokeMessage extends MessageContent implements Serializable {

    private String content;

    public static RevokeMessage obtain(Message oldMessage) {
        RevokeMessage body = new RevokeMessage();
        body.setContent(oldMessage.toJson());
        return body;
    }

    @Override
    public MessageContent parseContent(JSONObject obj) {
        if (obj != null) {
            setContent(obj.optString("content"));
        }
        return this;
    }

    public String getContent() {
        return ChatNull.compat(content);
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Nullable
    public Message getRevokeMessage() {
        return Message.parseMessageFromJsonOrNull(getContent());
    }

    @Override
    public Spannable getSummarySpannable(Context context) {
        return null;
    }
}
