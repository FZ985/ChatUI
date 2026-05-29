package io.im.lib.callback;


/**
 * author : JFZ
 * date : 2023/12/21 14:04
 * description :
 */
public interface MessageCallback<T> {

    void onSuccess(T message);

    void onError(T message, int errorCode);

    default void onProgress(T message, int progress) {
    }

}
