package io.im.core.model;

import android.content.Context;
import android.text.Spannable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;

import io.im.core.utils.ChatLibUtil;

/**
 * author : JFZ
 * date : 2024/1/27 11:38
 * description :
 */
@Keep
public abstract class MessageContent implements Serializable {

    public int extType;
    public String extData;

    public MessageContent() {
    }

    public int getExtType() {
        return extType;
    }

    public void setExtType(int extType) {
        this.extType = extType;
    }

    public String getExtData() {
        return extData;
    }

    public void setExtData(String extData) {
        this.extData = extData;
    }

    MessageContent parseJsonToContent(JSONObject obj) {
        if (obj != null) {
            if (obj.has("extType")) {
                setExtType(obj.optInt("extType"));
            }
            if (obj.has("extData")) {
                setExtData(obj.optString("extData"));
            }
        }
        return parseContent(obj);
    }

    public abstract MessageContent parseContent(JSONObject obj);

    public String toJson() {
        return ChatLibUtil.toJson(this);
    }

    @NonNull
    @Override
    public String toString() {
        return toJson();
    }


    public MessageContent copy() {
        return ChatLibUtil.deepCopy(this);
    }

    public abstract Spannable getSummarySpannable(Context context);
}
