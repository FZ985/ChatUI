package io.im.core.message.im;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Keep;

import org.json.JSONObject;

import java.io.Serializable;

import io.im.core.model.MessageContent;
import io.im.core.utils.FileUtils;

/**
 * author : JFZ
 * date : 2024/2/19 09:21
 * description :
 */
@Keep
public class MediaMessage extends MessageContent implements Serializable {

    private String url;
    private String localUri;

    @Override
    public MessageContent parseContent(JSONObject obj) {
        if (obj != null) {
            setUrl(obj.optString("url"));
            setLocalUri(obj.optString("localUri"));
        }
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public boolean isLocalExit(Context context) {
        boolean notExit =
                (getLocalUri() == null
                        || TextUtils.isEmpty(getLocalUri())
                        || !FileUtils.isFileExistsWithUri(
                        context, Uri.parse(getLocalUri())));
        return !notExit;
    }
}
