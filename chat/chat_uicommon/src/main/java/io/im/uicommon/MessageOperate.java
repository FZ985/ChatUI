package io.im.uicommon;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.im.core.MessageType;
import io.im.core.core.ChatSDK;
import io.im.core.listener.ChatFun;
import io.im.core.listener.MessageCallback;
import io.im.core.message.im.ForwardMessage;
import io.im.core.message.im.HQVoiceMessage;
import io.im.core.message.im.RevokeMessage;
import io.im.core.model.ConversationType;
import io.im.core.model.Message;
import io.im.core.model.UserInfo;
import io.im.core.utils.ChatToast;
import io.im.core.utils.ServeTime;
import io.im.uicommon.bean.AudioDataBean;
import io.im.uicommon.event.ChatMessageEvent;
import io.im.uicommon.listener.MessageEventListener;
import io.im.uicommon.manager.MessageManager;

/**
 * by DAD FZ
 * 2026/5/22
 * desc：消息操作
 **/
public class MessageOperate {

    //撤销消息
    public static void revokeMessage(ConversationType conversationType,
                                     UserInfo user, @NonNull Message oldMessage) {
        RevokeMessage revokeMessage = RevokeMessage.obtain(oldMessage);
        Message message = Message.obtain(user, conversationType, MessageType.CHAT_REVOKE, revokeMessage);
        //将原位置的消息id给到最新的message对象
        message.setMessageId(oldMessage.getMessageId());
        sendMessage(message, null, true, false, null);
    }

    //合并转发消息
    public static void sendMergeForwardMessage(ConversationType conversationType,
                                               UserInfo user, List<Message> messageList, List<UserInfo> users, @Nullable MessageCallback<Message> callback) {
        ForwardMessage forward = ForwardMessage.obtain(
                IMCenter.getLoginUser().getName(),
                user.getName(),
                messageList);
        for (UserInfo u : users) {
            Message message = Message.obtain(u,
                    conversationType,
                    MessageType.CHAT_FORWARD,
                    forward
            );
            sendMessage(message, null, callback);
        }
    }

    //逐条发送消息
    public static void sendForwardMessage(List<Message> messageList, List<UserInfo> users, @NonNull ChatFun.Fun2<List<Message>, List<Message>> callback) {
        if (messageList.isEmpty()) return;
        for (UserInfo user : users) {
            List<Message> msgList = new ArrayList<>();
            for (int i = 0; i < messageList.size(); i++) {
                Message m = messageList.get(i);
                Message newMsg = Message.obtain(user, m.getConversationType(), m.getMessageType(), m.getMessageContent());
                newMsg.setCreateTime(ServeTime.currentTimeMillis() + i);
                newMsg.setReplyMessage(m.getReplyMessage());
                msgList.add(newMsg);
            }
            sendForwardMessage(msgList, new ArrayList<>(), new ArrayList<>(), callback);
        }
    }

    //多条转发消息发送
    private static void sendForwardMessage(List<Message> messageList,
                                           List<Message> successMessage, List<Message> errorMessage,
                                           @NonNull ChatFun.Fun2<List<Message>, List<Message>> callback) {
        if (!messageList.isEmpty()) {
            Message message = messageList.get(0);
            sendMessage(message, null, new MessageCallback<Message>() {
                @Override
                public void onSuccess(Message message) {
                    successMessage.add(message);
                    messageList.remove(0);
                    sendForwardMessage(messageList, successMessage, errorMessage, callback);
                }

                @Override
                public void onError(Message message, int errorCode) {
                    errorMessage.add(message);
                    messageList.remove(0);
                    sendForwardMessage(messageList, successMessage, errorMessage, callback);
                }
            });
        } else {
            callback.apply(successMessage, errorMessage);
        }
    }

    //发送消息
    public static void sendMessage(Message message, @Nullable Message replayMessage, @Nullable MessageCallback<Message> callback) {
        sendMessage(message, replayMessage, true, true, callback);
    }

    //发送消息
    public static void sendMessage(Message message, @Nullable Message replyMessage, boolean postEvent, boolean postAttach, @Nullable MessageCallback<Message> callback) {
        if (replyMessage != null) {
            message.setReplyMessage(replyMessage.toJson());
        }
        if (postEvent && postAttach) {
            postSendEvent(new io.im.uicommon.event.ChatMessageEvent(io.im.uicommon.event.ChatMessageEvent.ATTACH, message));
        }
        MessageManager.getInstance().sendMessage(message, new MessageCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                if (postEvent) {
                    if (MessageType.isAppType(message.getMessageType())) {
                        postSendOtherMessage(new io.im.uicommon.event.ChatMessageEvent(io.im.uicommon.event.ChatMessageEvent.SUCCESS, message));
                    } else {
                        postSendEvent(new io.im.uicommon.event.ChatMessageEvent(io.im.uicommon.event.ChatMessageEvent.SUCCESS, message));
                    }
                }
                if (callback != null) {
                    callback.onSuccess(message);
                }
            }

            @Override
            public void onError(Message message, int errorCode) {
                if (postEvent) {
                    if (MessageType.isAppType(message.getMessageType())) {
                        postSendOtherMessage(new io.im.uicommon.event.ChatMessageEvent(io.im.uicommon.event.ChatMessageEvent.ERROR, message));
                    } else {
                        postSendEvent(new io.im.uicommon.event.ChatMessageEvent(io.im.uicommon.event.ChatMessageEvent.ERROR, message));
                    }
                }
                if (callback != null) {
                    callback.onError(message, errorCode);
                }
            }
        });
    }


    //删除消息
    public static void deleteMessage(Message message, @Nullable MessageCallback<Message> callback) {
        List<Message> messageList = new ArrayList<>();
        messageList.add(message);
        deleteMessage(messageList, callback);
    }

    //删除消息
    public static void deleteMessage(List<Message> messageList, @Nullable MessageCallback<Message> callback) {
        postDeleteMessage(new io.im.uicommon.event.DeleteMessageEvent(io.im.uicommon.event.DeleteMessageEvent.SUCCESS, messageList));
    }

    //发送语音消息
    public static void sendVoiceMessage(UserInfo toUser, ConversationType conversationType, AudioDataBean voiceData, @Nullable Message replyMessage, @Nullable MessageCallback<Message> callback) {
        HQVoiceMessage voiceBody = HQVoiceMessage.obtain(voiceData.getUrl(), voiceData.getPath(), voiceData.getDuration());
        Message message = Message.obtain(toUser, conversationType, MessageType.CHAT_VOICE, voiceBody);
        postSendMediaMessage(new ChatMessageEvent(ChatMessageEvent.PROGRESS, message, 0));
        sendMessage(message, replyMessage, callback);
    }

//    //发送阅读消息
//    public void sendReadMessage(Message message, MessageCallback callback) {
//        message.setType(MessageType.READ_MESSAGE);
//        sendMessage(message, callback);
//    }
//
//    public void sendReadMessage(UserInfo user, Conversation.ConversationType type) {
//        Message message = Message.obtain(user, type, MessageType.READ_MESSAGE, ReadMsgMessage.empty());
//        sendMessage(message, null);
//    }
//
//    //发送撤回消息
//    public void sendRevokeMessage(Message message, MessageCallback callback) {
//        message.setType(MessageType.REVOKE_705);
//        sendMessage(message, callback);
//    }
//
//
//    //发送订单消息
//    public void sendOrderMessage(UserInfo user, Conversation.ConversationType type, OrderMessage body, MessageCallback callback) {
//        Message message = Message.obtain(user, type, MessageType.ORDER, body);
//        List<MessageEventListener> eventListeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
//        for (MessageEventListener listener : eventListeners) {
//            if (listener != null) {
//                listener.onSendMessage(new SendEvent(SendEvent.ATTACH, message));
//            }
//        }
//        sendMessage(message, callback);
//    }
//
//    //发送商品消息
//    public void sendGoodsMessage(UserInfo user, Conversation.ConversationType type, GoodsMessage goodsMessage, MessageCallback callback) {
//        Message message = Message.obtain(user, type, MessageType.GOODS, goodsMessage);
//        List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
//        for (MessageEventListener listener : listeners) {
//            listener.onSendMessage(new SendEvent(SendEvent.ATTACH, message));
//        }
//        sendMessage(message, new MessageCallback() {
//            @Override
//            public void onSuccess(Message message) {
//                List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
//                for (MessageEventListener listener : listeners) {
//                    listener.onSendMessage(new SendEvent(SendEvent.SUCCESS, message));
//                }
//                if (callback != null) {
//                    callback.onSuccess(message);
//                }
//            }
//
//            @Override
//            public void onError(Message message, int errorCode) {
//                List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
//                for (MessageEventListener listener : listeners) {
//                    listener.onSendMessage(new SendEvent(SendEvent.ERROR, message));
//                }
//                if (callback != null) {
//                    callback.onError(message, errorCode);
//                }
//            }
//        });
//    }
//
//
//    //上传并发送消息
//    public void uploadAndSendMediaMessage(Message message, File file) {
//        List<MessageEventListener> eventListeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
//        for (MessageEventListener listener : eventListeners) {
//            listener.onSendMediaMessage(new SendMediaEvent(SendMediaEvent.PROGRESS, message, 0));
//        }
//        UploadModel.get().uploadFile(file, (url) -> {
//            MediaMessage body = (MediaMessage) message.getBody();
//            for (MessageEventListener listener : eventListeners) {
//                body.setContent(url);
//                message.setBody(body);
//                MessageManager.getInstance().sendMessage(message, new MessageCallback() {
//                    @Override
//                    public void onSuccess(Message message1) {
//                        message1.setStatus(State.SUCCESS);
//                        listener.onSendMediaMessage(new SendMediaEvent(SendMediaEvent.SUCCESS, message1));
//                    }
//
//                    @Override
//                    public void onError(Message message1, int errorCode) {
//                        message1.setStatus(State.ERROR);
//                        listener.onSendMediaMessage(new SendMediaEvent(SendMediaEvent.ERROR, message1));
//                    }
//                });
//            }
//        }, () -> {
//            ResendManager.getInstance().addResendMessage(message, false);
//            for (MessageEventListener listener : eventListeners) {
//                message.setStatus(State.ERROR);
//                listener.onSendMediaMessage(new SendMediaEvent(SendMediaEvent.ERROR, message));
//            }
//        }, (progress) -> {
//            for (MessageEventListener listener : eventListeners) {
//                message.setStatus(State.PROGRESS);
//                listener.onSendMediaMessage(new SendMediaEvent(SendMediaEvent.PROGRESS, message, progress));
//            }
//        });
//    }
//
//    //上传并发送消息
//    public void uploadAndSendMediaMessage(Message message, Picker picker) {
//        List<MessageEventListener> eventListeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
//        for (MessageEventListener listener : eventListeners) {
//            listener.onSendMediaMessage(new SendMediaEvent(SendMediaEvent.PROGRESS, message, 0));
//        }
//        MediaMessage body = (MediaMessage) message.getBody();
//        UploadModel.get().upload(ChatSDK.getContext(), picker, (p, url) -> {
//            for (MessageEventListener listener : eventListeners) {
//                body.setContent(url);
//                message.setBody(body);
//                MessageManager.getInstance().sendMessage(message, new MessageCallback() {
//                    @Override
//                    public void onSuccess(Message message) {
//                        message.setStatus(State.SUCCESS);
//                        listener.onSendMediaMessage(new SendMediaEvent(SendMediaEvent.SUCCESS, message));
//                    }
//
//                    @Override
//                    public void onError(Message message, int errorCode) {
//                        message.setStatus(State.ERROR);
//                        listener.onSendMediaMessage(new SendMediaEvent(SendMediaEvent.ERROR, message));
//                    }
//                });
//            }
//        }, p -> {
//            ResendManager.getInstance().addResendMessage(message, false);
//            for (MessageEventListener listener : eventListeners) {
//                message.setStatus(State.ERROR);
//                listener.onSendMediaMessage(new SendMediaEvent(SendMediaEvent.ERROR, message));
//            }
//        }, (p, progress) -> {
//            for (MessageEventListener listener : eventListeners) {
//                message.setStatus(State.PROGRESS);
//                listener.onSendMediaMessage(new SendMediaEvent(SendMediaEvent.PROGRESS, message, progress));
//            }
//        });
//    }


    //分发发送事件
    public static void postSendEvent(io.im.uicommon.event.ChatMessageEvent event) {
        List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
        try {
            for (MessageEventListener listener : listeners) {
                if (listener != null) {
                    listener.onSendMessage(event);
                }
            }
        } catch (Exception e) {
            //
        }
    }

    //分发发送媒体消息回调事件
    public static void postSendMediaMessage(io.im.uicommon.event.ChatMessageEvent event) {
        List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
        try {
            for (MessageEventListener listener : listeners) {
                if (listener != null) {
                    listener.onSendMediaMessage(event);
                }
            }
        } catch (Exception e) {
            //
        }
    }

    //分发发送的其他消息回调事件
    public static void postSendOtherMessage(io.im.uicommon.event.ChatMessageEvent event) {
        List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
        try {
            for (MessageEventListener listener : listeners) {
                if (listener != null) {
                    listener.onSendOtherMessage(event);
                }
            }
        } catch (Exception e) {
            //
        }
    }

    //分发接收消息回调事件
    public static void postReceiveMessage(io.im.uicommon.event.ChatMessageEvent event) {
        List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
        try {
            for (MessageEventListener listener : listeners) {
                if (listener != null) {
                    listener.onReceiveMessage(event);
                }
            }
        } catch (Exception e) {
            //
        }
    }

    //分发接收的其他消息回调事件
    public static void postReceiveOtherMessage(io.im.uicommon.event.ChatMessageEvent event) {
        List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
        try {
            for (MessageEventListener listener : listeners) {
                if (listener != null) {
                    listener.onReceiveOtherMessage(event);
                }
            }
        } catch (Exception e) {
            //
        }
    }


    //分发删除消息的回调事件
    public static void postDeleteMessage(io.im.uicommon.event.DeleteMessageEvent event) {
        List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
        try {
            for (MessageEventListener listener : listeners) {
                if (listener != null) {
                    listener.onDeleteMessage(event);
                }
            }
        } catch (Exception e) {
            //
        }
    }


    /**
     * 复制文本到剪切板
     *
     * @param text      文本内容
     * @param showToast 是否显示Toast提示
     */
    public static void copyText(String text, boolean showToast) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        ClipboardManager cmb = (ClipboardManager) ChatSDK.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, text);
        if (clipData != null) {
            cmb.setPrimaryClip(clipData);
            if (showToast) {
                ChatToast.toast(ChatSDK.getContext(), R.string.chat_message_action_copy_success);
            }
        }
    }
}
