package io.im.lib.core.socket;

import java.io.Serializable;

/**
 * author : JFZ
 * date : 2023/12/22 17:30
 * description :
 */
public class ErrorResult implements Serializable {

    private int code;

    private String errorMsg;

    public ErrorResult(int code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
