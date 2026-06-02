package io.im.core.message.im;


import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;

import androidx.annotation.Keep;

import org.json.JSONObject;

import java.io.Serializable;

import io.im.core.model.MessageContent;

/**
 * by DAD FZ
 * 2026/6/2
 * desc：高清语音消息
 **/
@Keep
public class HQVoiceMessage extends MediaMessage implements Serializable {

    private long duration;

    private boolean isListened;//是否听过

    public static HQVoiceMessage obtain(String url, String path, long duration) {
        HQVoiceMessage body = new HQVoiceMessage();
        body.setUrl(url);
        body.setLocalPath(path);
        body.setDuration(duration);
        return body;
    }

    @Override
    public MessageContent parseContent(JSONObject obj) {
        super.parseContent(obj);
        setDuration(obj.optLong("duration"));
        setListened(obj.optBoolean("isListened", false));
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isListened() {
        return isListened;
    }

    public void setListened(boolean listened) {
        isListened = listened;
    }

    @Override
    public Spannable getSummarySpannable(Context context) {
        return new SpannableString("[语音]");
    }
}
