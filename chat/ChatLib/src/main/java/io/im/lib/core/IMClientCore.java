package io.im.lib.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.im.lib.callback.ChatFun;
import io.im.lib.core.socket.ErrorResult;
import io.im.lib.core.socket.SocketCode;
import io.im.lib.core.socket.WebSocketResult;
import io.im.lib.listener.OnSocketMessageListener;
import io.im.lib.model.Message;
import io.im.lib.utils.ChatLibUtil;

/**
 * author : JFZ
 * date : 2023/12/22 11:48
 * description :
 */
public class IMClientCore {

    private static IMClientCore client;

    private IMClientCore() {

    }

    private final List<OnSocketMessageListener> onSocketMessageListeners = new ArrayList<>();

    private final OnSocketMessageListener innerSocketListener = new OnSocketMessageListener() {
        @Override
        public void onMessage(@NonNull Message message, int code) {
            try {
                for (OnSocketMessageListener onMessageReceiveListener : onSocketMessageListeners) {
                    onMessageReceiveListener.onMessage(message, code);
                }
            } catch (Exception e) {
                //
            }
        }

        @Override
        public void onMessageError(@Nullable Message message, ErrorResult error) {
            try {
                for (OnSocketMessageListener onMessageReceiveListener : onSocketMessageListeners) {
                    onMessageReceiveListener.onMessageError(message, error);
                }
            } catch (Exception e) {
                //
            }
        }
    };

    private final ChatFun.Fun1<WebSocketResult> handlerSocketResponse = result -> {
        if (result.getStatus() == SocketCode.SOCKET_MESSAGE) {
            String data = result.getData();
            if (ChatLibUtil.isJson(data)) {
                Message message = Message.parseMessageFromJson(data);
                try {
                    JSONObject obj = new JSONObject(data);
                    if (obj.has("code")) {
                        int code = obj.optInt("code");
                        String msg = obj.optString("errorMsg");
                        if (code == 200) {
                            //成功的消息体
                            innerSocketListener.onMessage(message, code);
                        } else {
                            ErrorResult error = new ErrorResult(code, msg);
                            innerSocketListener.onMessageError(message, error);
                        }
                    }
                } catch (JSONException e) {
                    innerSocketListener.onMessageError(message, new ErrorResult(SocketCode.JSON_INVALID_DATA, e.getMessage()));
                }
            }
        } else if (result.getStatus() == SocketCode.SOCKET_SEND_ERROR) {
//            JLog.e("=====消息发送失败=====");
            Message message = Message.parseMessageFromJson(result.getData());
            message.setSendStatus(Message.SentStatus.FAILED.getValue());
            innerSocketListener.onMessageError(message, new ErrorResult(result.getStatus(), result.getData()));
        } else {
            innerSocketListener.onMessageError(null, new ErrorResult(result.getStatus(), result.getData()));
        }
    };

    public static IMClientCore getInstance() {
        if (client == null) {
            synchronized (IMClientCore.class) {
                if (client == null) {
                    client = new IMClientCore();
                }
            }
        }
        return client;
    }


    public ChatFun.Fun1<WebSocketResult> getHandlerSocketResponse() {
        return handlerSocketResponse;
    }

    //发送socket消息
    public void senMessage(Message message) {
        CoreSingle.getInstance().sendMessage(message);
    }

    //关闭socket
    public void closeSocket() {
        CoreSingle.getInstance().closeSocket();
    }

    //添加socket监听
    public void setOnSocketMessageListener(OnSocketMessageListener onMessageReceiveListener) {
        if (!onSocketMessageListeners.contains(onMessageReceiveListener)) {
            onSocketMessageListeners.add(onMessageReceiveListener);
        }
    }

    //移除socket监听
    public void removeOnSocketMessageListener(OnSocketMessageListener listener) {
        onSocketMessageListeners.remove(listener);
    }

    //发送一个错误，统一分发给各个socket监听
    public void sendError(ErrorResult errorResult) {
        innerSocketListener.onMessageError(null, errorResult);
    }

    //发送一个错误，统一分发给各个socket监听
    public void sendError(Message message, ErrorResult errorResult) {
        innerSocketListener.onMessageError(message, errorResult);
    }

}
