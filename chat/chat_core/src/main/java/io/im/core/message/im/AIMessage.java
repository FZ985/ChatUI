package io.im.core.message.im;

import android.content.Context;
import android.text.Spannable;

import androidx.annotation.Keep;

import org.json.JSONObject;

import java.io.Serializable;

import io.im.core.model.MessageContent;
import io.im.core.utils.ChatNull;

/**
 * author : JFZ
 * date : 2026/08/19 09:42
 * description :
 */
@Keep
public final class AIMessage extends MessageContent implements Serializable {

    public static final int TYPE_AI_MESSAGE = 2000;

    private String content;
    private boolean isGenerating = false;

    public static AIMessage obtain(String content) {
        AIMessage body = new AIMessage();
        body.setContent(content);
        return body;
    }

    @Override
    public MessageContent parseContent(JSONObject obj) {
        if (obj != null) {
            setContent(obj.optString("content"));
            setGenerating(obj.optBoolean("isGenerating", false));
        }
        return this;
    }

    public boolean isGenerating() {
        return isGenerating;
    }

    public void setGenerating(boolean generating) {
        isGenerating = generating;
    }

    public String getContent() {
        return ChatNull.compat(content);
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public Spannable getSummarySpannable(Context context) {
        return null;
    }
}
