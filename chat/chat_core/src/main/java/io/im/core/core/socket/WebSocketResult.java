package io.im.core.core.socket;

import java.io.Serializable;

import io.im.core.utils.ChatLibUtil;


/**
 * author : JFZ
 * date : 2023/12/22 11:38
 * description :
 */
public class WebSocketResult implements Serializable {

    private int status;

    private String data;

    public WebSocketResult(int status, String data) {
        this.status = status;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String toJson() {
        return ChatLibUtil.toJson(this);
    }

}
