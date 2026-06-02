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
public abstract class MediaMessage extends MessageContent implements Serializable {

    private String url;
    private String localPath;

    @Override
    public MessageContent parseContent(JSONObject obj) {
        if (obj != null) {
            setUrl(obj.optString("url"));
            setLocalPath(obj.optString("localPath"));
        }
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Uri getLocalUri() {
        return Uri.parse(getLocalPath());
    }


    public boolean isLocalExit(Context context) {
        boolean notExit =
                (getLocalPath() == null
                        || TextUtils.isEmpty(getLocalPath())
                        || !FileUtils.isFileExistsWithUri(
                        context, getLocalUri()));
        return !notExit;
    }
}
