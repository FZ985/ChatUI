package io.im.uicommon.config;


import java.util.ArrayList;
import java.util.List;

import io.im.core.core.socket.ErrorResult;
import io.im.core.core.socket.SocketCode;
import io.im.core.listener.OnConnectListener;

/**
 * by DAD FZ
 * 2026/5/22
 * desc：全局连接监听
 **/
public class ConnectService {

    private ErrorResult currentResult;

    private final OnConnectListener innerConnectListener = new OnConnectListener() {
        @Override
        public void onConnected(ErrorResult result) {
            try {
                currentResult = result;
                if (!connectListener.isEmpty()) {
                    for (OnConnectListener conn : connectListener) {
                        if (conn != null) {
                            conn.onConnected(result);
                        }
                    }
                } else {
                    connectCaches.add(result);
                }
            } catch (Exception e) {

            }
        }
    };

    //是否连接
    public boolean isConnect() {
        if (currentResult != null) {
            return currentResult.getCode() == SocketCode.NETWORK_SUCCESS
                    || currentResult.getCode() == SocketCode.SOCKET_PING_SUCCESS
                    || currentResult.getCode() == SocketCode.SOCKET_OPEN;
        }
        return false;
    }

    private final List<OnConnectListener> connectListener = new ArrayList<>();
    private final List<ErrorResult> connectCaches = new ArrayList<>();

    public OnConnectListener getConnectListener() {
        return innerConnectListener;
    }

    public void addConnectListener(OnConnectListener connectListener) {
        if (connectListener != null) {
            this.connectListener.add(connectListener);
        }

        if (connectListener == null) {
            connectCaches.clear();
        }

        //实时回调给新添加的监听最新的状态
        if (!connectCaches.isEmpty()) {
            ErrorResult errorResult = connectCaches.get(connectCaches.size() - 1);
            connectListener.onConnected(errorResult);
            connectCaches.clear();
            connectCaches.add(errorResult);
        }
    }

    public void removeConnectListener(OnConnectListener connectListener) {
        this.connectListener.remove(connectListener);
    }


    public void clearAllConnectListener() {
        connectListener.clear();
        connectCaches.clear();
    }
}
