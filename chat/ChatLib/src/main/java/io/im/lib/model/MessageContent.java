package io.im.lib.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;

import io.im.lib.utils.ChatLibUtil;

/**
 * author : JFZ
 * date : 2024/1/27 11:38
 * description :
 */
@Keep
public abstract class MessageContent implements Serializable {
    public int extType;
    public String ext;

    public int getExtType() {
        return extType;
    }

    public void setExtType(int extType) {
        this.extType = extType;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    MessageContent parseJsonToContent(JSONObject obj) {
        if (obj != null) {
            if (obj.has("extType")) {
                setExtType(obj.optInt("extType"));
            }
            if (obj.has("ext")) {
                setExt(obj.optString("ext"));
            }
        }
        return parseContent(obj);
    }

    public abstract MessageContent parseContent(JSONObject obj);

    @NonNull
    @Override
    public String toString() {
        return ChatLibUtil.toJson(this);
    }
}
