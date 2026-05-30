package io.im.core.listener;


import io.im.core.core.socket.ErrorResult;

/**
 * author : JFZ
 * date : 2023/12/22 17:54
 * description :
 */
public interface OnConnectListener {

    void onConnected(ErrorResult result);
}
