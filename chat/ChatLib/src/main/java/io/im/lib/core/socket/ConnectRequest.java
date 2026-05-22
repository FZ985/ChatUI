package io.im.lib.core.socket;

import android.net.Uri;

import java.io.Serializable;
import java.util.HashMap;

/**
 * author : JFZ
 * date : 2023/12/21 14:11
 * description :
 */
public class ConnectRequest implements Serializable {

    private String url;

    private final HashMap<String, String> headMap = new HashMap<>();

    private final HashMap<String, String> paramsMap = new HashMap<>();

    public static ConnectRequest get() {
        return new ConnectRequest();
    }

    public ConnectRequest url(String url) {
        this.url = url;
        return this;
    }

    public ConnectRequest addHeader(String key, String value) {
        headMap.put(key, value);
        return this;
    }

    public ConnectRequest addParam(String key, String value) {
        paramsMap.put(key, value);
        return this;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getHeadMap() {
        return headMap;
    }

    public HashMap<String, String> getParamMap() {
        return paramsMap;
    }

    public void release() {
        url = "";
        headMap.clear();
        paramsMap.clear();
    }

    public String getFinalUrl() {
        Uri uri = Uri.parse(getUrl());
        Uri.Builder build = uri.buildUpon();
        for (String key : paramsMap.keySet()) {
            Object string = paramsMap.get(key);
            build.appendQueryParameter(key, string == null ? "" : string.toString());
        }
        Uri resultUri = build.build();
        return resultUri.toString();
    }
}
