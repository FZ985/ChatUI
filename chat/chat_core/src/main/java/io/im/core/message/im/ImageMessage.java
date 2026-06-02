package io.im.core.message.im;

import android.content.Context;
import android.text.Spannable;
import android.util.Size;

import androidx.annotation.Keep;

import org.json.JSONObject;

import java.io.Serializable;

import io.im.core.model.MessageContent;

/**
 * author : JFZ
 * date : 2024/2/19 09:23
 * description :
 */
@Keep
public class ImageMessage extends MediaMessage implements Serializable {

    private int width;

    private int height;

    public static ImageMessage obtain(String url, String localPath) {
        return obtain(url, localPath, null);
    }

    public static ImageMessage obtain(String url, String localPath, Size size) {
        ImageMessage message = new ImageMessage();
        message.setUrl(url);
        message.setLocalPath(localPath);
        if (size != null) {
            message.setWidth(size.getWidth());
            message.setHeight(size.getHeight());
        }
        return message;
    }

    @Override
    public MessageContent parseContent(JSONObject obj) {
        super.parseContent(obj);
        if (obj != null) {
            setWidth(obj.optInt("width"));
            setHeight(obj.optInt("height"));
        }
        return this;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public Spannable getSummarySpannable(Context context) {
        return null;
    }
}
