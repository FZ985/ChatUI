package io.im.lib.callback;


import io.im.lib.model.Message;

/**
 * author : JFZ
 * date : 2023/12/21 14:04
 * description :
 */
public interface MessageCallback {

    void onSuccess(Message message);

    void onError(Message message, int errorCode);

}
