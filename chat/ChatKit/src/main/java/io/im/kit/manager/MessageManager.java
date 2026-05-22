package io.im.kit.manager;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.im.kit.IMCenter;
import io.im.kit.event.actionevent.OtherMessageEvent;
import io.im.kit.event.actionevent.ReceiveMessageEvent;
import io.im.kit.event.actionevent.SendMessageEvent;
import io.im.kit.listener.MessageEventListener;
import io.im.kit.listener.MessageInterceptListener;
import io.im.lib.MessageType;
import io.im.lib.callback.SendMessageCallback;
import io.im.lib.core.IMClientCore;
import io.im.lib.core.socket.SocketCode;
import io.im.lib.listener.OnSocketMessageListener;
import io.im.lib.model.Message;
import io.im.lib.core.socket.ErrorResult;
import io.im.lib.utils.JLog;


/**
 * author : JFZ
 * date : 2023/12/21 14:02
 * description : 消息管理
 */
public class MessageManager {

    private final HashMap<Long, SendMessageCallback> callbackMap = new HashMap<>();

    private final List<OnSocketMessageListener> socketMessageListeners = new ArrayList<>();


    final OnSocketMessageListener socketMessageListener = new OnSocketMessageListener() {
        @Override
        public void onMessage(@NonNull Message message, int errorCode) {
            handlerMessage(message, errorCode);
        }

        @Override
        public void onMessageError(Message message, ErrorResult error) {
            handlerMessage(message, error.getCode());
        }
    };

    private MessageManager() {
        IMClientCore.getInstance().setOnSocketMessageListener(socketMessageListener);
    }

    private void handlerMessage(@Nullable Message message, int errorCode) {
        MessageInterceptListener interceptListener = IMCenter.getInstance().getOptions().getMessageInterceptListener();
        if (interceptListener != null && !interceptListener.onReceiveInterceptMessage(message, errorCode)) {
            if (message != null) {
                JLog.e("SocketCode=====message:" + message.toString());
                if (message.getMessageDirection() == Message.MessageDirection.SEND) {
                    filterSendMessage(message, errorCode);
                } else {
                    filterReceiveMessage(message, errorCode);
                }
                try {
                    for (OnSocketMessageListener listener : socketMessageListeners) {
                        listener.onMessage(message, errorCode);
                    }
                } catch (Exception e) {
                    //
                }
            }
        }
    }

    //发送的消息
    private void filterSendMessage(Message message, int errorCode) {
        message = transformMessage(message);
        SendMessageCallback callback = callbackMap.get(message.getMessageId());
        if (errorCode == SocketCode.success) {
//            ResendManager.getInstance().removeResendMessage(message.getMessageId());
            if (callback != null) {
                callback.onSuccess(message);
            }
            removeCallback(message);
            List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
            if (MessageType.isAppType(message.getMessageType())) {
                for (MessageEventListener listener : listeners) {
                    listener.onSendOtherMessage(new OtherMessageEvent(message));
                }
            } else {
                for (MessageEventListener listener : listeners) {
                    listener.onSendMessage(new SendMessageEvent(SendMessageEvent.SUCCESS, message));
                }
            }
        } else {
//            ResendManager.getInstance().addResendMessage(message, false);
            if (callback != null) {
                callback.onError(message, errorCode);
            }
            removeCallback(message);
            List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
            if (MessageType.isAppType(message.getMessageType())) {
                for (MessageEventListener listener : listeners) {
                    listener.onSendOtherMessage(new OtherMessageEvent(message));
                }
            } else {
                for (MessageEventListener listener : listeners) {
                    listener.onSendMessage(new SendMessageEvent(SendMessageEvent.ERROR, message));
                }
            }
        }
    }

    private void removeCallback(Message message) {
        callbackMap.remove(message.getMessageId());
    }

    public Message transformMessage(Message message) {
        return message;
    }

    //接收消息
    private void filterReceiveMessage(Message message, int errorCode) {
        List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
        if (MessageType.isAppType(message.getMessageType())) {
            for (MessageEventListener listener : listeners) {
                if (listener != null) {
                    listener.onReceiveOtherMessage(new OtherMessageEvent(message));
                }
            }
        } else {
            for (MessageEventListener listener : listeners) {
                if (listener != null) {
                    listener.onReceiveMessage(new ReceiveMessageEvent(message));
                }
            }
        }
    }

    static class SingletonHolder {
        static MessageManager sInstance = new MessageManager();
    }

    public static MessageManager getInstance() {
        return SingletonHolder.sInstance;
    }

    //发送消息,所有发送消息，都应该执行这个函数
    public void sendMessage(Message message, @Nullable SendMessageCallback callback) {
        JLog.e("发送的消息:" + message.toString());
        if (callback != null) {
            callbackMap.put(message.getMessageId(), callback);
        }
        MessageInterceptListener listener = IMCenter.getInstance().getOptions().getMessageInterceptListener();
        if (listener != null && !listener.onSendInterceptMessage(message)) {
            IMClientCore.getInstance().senMessage(message);
        }
    }

    //添加socket消息监听
    public void addOnSocketMessageListener(OnSocketMessageListener listener) {
        if (!socketMessageListeners.contains(listener)) {
            socketMessageListeners.add(listener);
        }
    }

    //移除socket消息监听
    public void removeOnSocketMessageListener(OnSocketMessageListener listener) {
        socketMessageListeners.remove(listener);
    }

}
