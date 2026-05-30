package io.im.core.core.socket;

import android.app.Service;
import android.os.RemoteException;

import io.im.core.core.CoreConstant;
import io.im.core.core.aidl.CoreResultInterface;
import io.im.core.utils.ChatLibUtil;
import okio.ByteString;

/**
 * author : JFZ
 * date : 2023/12/22 11:44
 * description :socket连接核心服务
 */
public abstract class ISocketCoreService extends Service {

    protected CoreResultInterface mCallback;

    protected void connectWebSocket(String data) {
        ConnectRequest request = ChatLibUtil.gson.fromJson(data, ConnectRequest.class);
        ISocketClient.getInstance().startConnect(request, new ISocketListener() {
            @Override
            public void onMessage(String text) {
                try {
                    if (mCallback != null) {
                        mCallback.onResult(CoreConstant.SocketResponse, new WebSocketResult(SocketCode.SOCKET_MESSAGE, text).toJson());
                    }
                } catch (RemoteException e) {

                }
            }

            @Override
            public void onPingSuccess() {
                try {
                    if (mCallback != null) {
                        mCallback.onResult(CoreConstant.SocketResponse, new WebSocketResult(SocketCode.SOCKET_PING_SUCCESS, "ping success").toJson());
                    }
                } catch (RemoteException e) {

                }
            }

            @Override
            public void onMessage(ByteString bytes) {

            }

            @Override
            public void onOpen(String msg) {
                try {
                    if (mCallback != null) {
                        mCallback.onResult(CoreConstant.SocketResponse, new WebSocketResult(SocketCode.SOCKET_OPEN, msg).toJson());
                    }
                } catch (RemoteException e) {

                }
            }

            @Override
            public void onClosed(String msg) {
                try {
                    if (mCallback != null) {
                        mCallback.onResult(CoreConstant.SocketResponse, new WebSocketResult(SocketCode.SOCKET_CLOSED, msg).toJson());
                    }
                } catch (RemoteException e) {

                }
            }

            @Override
            public void onClosing(String msg) {
                try {
                    if (mCallback != null) {
                        mCallback.onResult(CoreConstant.SocketResponse, new WebSocketResult(SocketCode.SOCKET_CLOSING, msg).toJson());
                    }
                } catch (RemoteException e) {

                }
            }

            @Override
            public void onFailure(Throwable t) {
                try {
                    if (mCallback != null && t != null) {
                        mCallback.onResult(CoreConstant.SocketResponse, new WebSocketResult(SocketCode.SOCKET_ERROR, t.getMessage()).toJson());
                    }
                } catch (RemoteException e) {

                }
            }
        });
    }

    protected void sendSocketData(String data) {
        try {
            boolean success = ISocketClient.getInstance().sendMessage(data);
            if (!success && mCallback != null) {
                mCallback.onResult(CoreConstant.SocketResponse, new WebSocketResult(SocketCode.SOCKET_SEND_ERROR, data).toJson());
            }
        } catch (RemoteException e) {

        }
    }

    protected boolean checkSocket() {
        return ISocketClient.getInstance().sendPing();
    }

    protected void closeSocket() {
        ISocketClient.getInstance().destroy();
    }
}
