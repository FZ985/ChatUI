package io.im.core.message;

import android.content.Context;
import android.text.Spannable;

import androidx.annotation.Keep;

import org.json.JSONObject;

import io.im.core.model.MessageContent;

/**
 * author : JFZ
 * date : 2024/1/27 11:46
 * description : 未知消息类型
 */
@Keep
public final class UnKnowMessage extends MessageContent {
    @Override
    public MessageContent parseContent(JSONObject obj) {
        return this;
    }


    public static UnKnowMessage obtain() {
        return new UnKnowMessage();
    }

    @Override
    public Spannable getSummarySpannable(Context context) {
        return null;
    }
}
