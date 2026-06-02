package io.im.uicommon.bean;


/**
 * by DAD FZ
 * 2026/6/1
 * desc：
 **/
public class AudioDataBean {

    private String url;

    private final String path;

    private final long duration;

    public AudioDataBean(String path, long duration) {
        this.path = path;
        this.duration = duration;
    }

    public String getUrl() {
        if (url == null) {
            url = "";
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public long getDuration() {
        return duration;
    }


}
