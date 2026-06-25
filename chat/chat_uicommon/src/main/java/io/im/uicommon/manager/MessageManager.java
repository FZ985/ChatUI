package io.im.uicommon.manager;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.im.core.MessageType;
import io.im.core.core.ChatSDK;
import io.im.core.core.IMClientCore;
import io.im.core.core.socket.ErrorResult;
import io.im.core.core.socket.SocketCode;
import io.im.core.listener.MessageCallback;
import io.im.core.listener.OnSocketMessageListener;
import io.im.core.model.Message;
import io.im.core.model.MessageUser;
import io.im.core.model.Session;
import io.im.core.utils.ChatExecutorHelper;
import io.im.core.utils.ConversationIdUtil;
import io.im.core.utils.JLog;
import io.im.uicommon.IMCenter;
import io.im.uicommon.MessageOperate;
import io.im.uicommon.event.ChatMessageEvent;
import io.im.uicommon.listener.MessageInterceptListener;


/**
 * author : JFZ
 * date : 2023/12/21 14:02
 * description : 消息管理
 */
public class MessageManager {

    private final HashMap<Long, MessageCallback<Message>> callbackMap = new HashMap<>();

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
        if (interceptListener != null && interceptListener.onReceiveInterceptMessage(message, errorCode)) {
            return;
        }
        if (message != null) {
            JLog.e(message.getMessageDirection() + "<<SocketCode=====message:" + message.toString());
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

    //发送的消息
    private void filterSendMessage(Message message, int errorCode) {
        message = transformMessage(message);
        MessageCallback<Message> callback = callbackMap.get(message.getMessageId());
        if (errorCode == SocketCode.success) {
//            ResendManager.getInstance().removeResendMessage(message.getMessageId());
            if (callback != null) {
                callback.onSuccess(message);
                if (!MessageType.isAppType(message.getMessageType())) {
                    checkSession(message);
                }
            } else {
                if (MessageType.isAppType(message.getMessageType())) {
                    MessageOperate.postSendOtherMessage(new ChatMessageEvent(ChatMessageEvent.SUCCESS, message));
                } else {
                    MessageOperate.postSendEvent(new ChatMessageEvent(ChatMessageEvent.SUCCESS, message));
                    checkSession(message);
                }
            }
            removeCallback(message);
        } else {
//            ResendManager.getInstance().addResendMessage(message, false);
            if (callback != null) {
                callback.onError(message, errorCode);
            } else {
                if (MessageType.isAppType(message.getMessageType())) {
                    MessageOperate.postSendOtherMessage(new ChatMessageEvent(ChatMessageEvent.ERROR, message));
                } else {
                    MessageOperate.postSendEvent(new ChatMessageEvent(ChatMessageEvent.ERROR, message));
                }
            }
            removeCallback(message);

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
        if (MessageType.isAppType(message.getMessageType())) {
            MessageOperate.postReceiveOtherMessage(new ChatMessageEvent(ChatMessageEvent.SUCCESS, message));
        } else {
            MessageOperate.postReceiveMessage(new ChatMessageEvent(ChatMessageEvent.SUCCESS, message));
        }
    }

    static class SingletonHolder {
        static MessageManager sInstance = new MessageManager();
    }

    public static MessageManager getInstance() {
        return SingletonHolder.sInstance;
    }

    //发送消息,所有发送消息，都应该执行这个函数
    public void sendMessage(Message message, @Nullable MessageCallback<Message> callback) {
        if (callback != null) {
            callbackMap.put(message.getMessageId(), callback);
        }
        MessageInterceptListener listener = IMCenter.getInstance().getOptions().getMessageInterceptListener();
        if (listener != null && listener.onSendInterceptMessage(message)) {
            return;
        }
        IMClientCore.getInstance().senMessage(message);
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

    //验证会话并创建
    private void checkSession(Message message) {
        String myAccount = IMCenter.getAccountId();
        MessageUser toUser = message.getFromUser().getId().equals(myAccount) ? message.getToUser() : message.getFromUser();
        String friendAccount = toUser.getId();
        ChatExecutorHelper.getInstance().diskIO().execute(() -> {
            String sid = ConversationIdUtil.conversationId(friendAccount, message.getConversationType());
            var session = ChatSDK.getDbManager().sessionDao().getSessionBySid(sid);
            if (session == null) {
                session = Session.obtain(
                        toUser.toUserInfo(),
                        message.getConversationType(),
                        message
                );
                ChatSDK.getDbManager().sessionDao().insertSession(session);
            }
        });
    }

}
