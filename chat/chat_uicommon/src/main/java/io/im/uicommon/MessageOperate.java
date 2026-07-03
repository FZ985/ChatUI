package io.im.uicommon;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.im.core.MessageType;
import io.im.core.listener.ChatFun;
import io.im.core.listener.FetchCallback;
import io.im.core.listener.MessageCallback;
import io.im.core.message.im.ForwardMessage;
import io.im.core.message.im.HQVoiceMessage;
import io.im.core.message.im.MediaMessage;
import io.im.core.message.im.RevokeMessage;
import io.im.core.model.ConversationType;
import io.im.core.model.Message;
import io.im.core.model.State;
import io.im.core.model.User;
import io.im.core.utils.ServeTime;
import io.im.uicommon.bean.AudioDataBean;
import io.im.uicommon.event.ChatMessageEvent;
import io.im.uicommon.event.DeleteMessageEvent;
import io.im.uicommon.helper.PostMessageEvent;
import io.im.uicommon.listener.UploadDownloadProcessor;
import io.im.uicommon.manager.MessageManager;
import io.im.uicommon.repo.ChatRepo;
import io.im.uicommon.resend.ResendManager;

/**
 * by DAD FZ
 * 2026/5/22
 * desc：消息操作
 **/
public class MessageOperate {

    //发送消息
    public static void sendMessage(Message message, @Nullable Message referMessage, @Nullable MessageCallback<Message> callback) {
        sendMessage(message, referMessage, true, true, callback);
    }

    //发送消息
    public static void sendMessage(Message message, @Nullable Message referMessage, boolean postEvent, boolean postAttach, @Nullable MessageCallback<Message> callback) {
        if (referMessage != null) {
            message.setReferMessage(referMessage.toJson());
        }
        if (postEvent && postAttach) {
            PostMessageEvent.postSendEvent(new io.im.uicommon.event.ChatMessageEvent(io.im.uicommon.event.ChatMessageEvent.ATTACH, message));
        }
        MessageManager.getInstance().sendMessage(message, new MessageCallback<>() {
            @Override
            public void onSuccess(Message message) {
                if (postEvent) {
                    if (MessageType.isAppType(message.getMessageType())) {
                        PostMessageEvent.postSendOtherMessage(new io.im.uicommon.event.ChatMessageEvent(io.im.uicommon.event.ChatMessageEvent.SUCCESS, message));
                    } else {
                        PostMessageEvent.postSendEvent(new io.im.uicommon.event.ChatMessageEvent(io.im.uicommon.event.ChatMessageEvent.SUCCESS, message));
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
                        PostMessageEvent.postSendOtherMessage(new io.im.uicommon.event.ChatMessageEvent(io.im.uicommon.event.ChatMessageEvent.ERROR, message));
                    } else {
                        PostMessageEvent.postSendEvent(new io.im.uicommon.event.ChatMessageEvent(io.im.uicommon.event.ChatMessageEvent.ERROR, message));
                    }
                }
                if (callback != null) {
                    callback.onError(message, errorCode);
                }
            }
        });
    }

    //发送->撤销消息
    public static void sendRevokeMessage(ConversationType conversationType,
                                         User user, @NonNull Message oldMessage) {
        RevokeMessage revokeMessage = RevokeMessage.obtain(oldMessage);
        Message message = Message.obtain(user, conversationType, MessageType.CHAT_REVOKE, revokeMessage);
        //将原位置的消息id给到最新的message对象
        message.setMessageId(oldMessage.getMessageId());
        sendMessage(message, null, true, false, null);
    }

    //发送->合并转发消息
    public static void sendMergeForwardMessage(ConversationType conversationType,
                                               User user, List<Message> messageList, List<User> users, @Nullable MessageCallback<Message> callback) {
        ForwardMessage forward = ForwardMessage.obtain(
                IMCenter.getLoginUser().getName(),
                user.getName(),
                messageList);
        for (User u : users) {
            Message message = Message.obtain(u,
                    conversationType,
                    MessageType.CHAT_FORWARD,
                    forward
            );
            sendMessage(message, null, callback);
        }
    }

    //发送->逐条发送消息
    public static void sendForwardMessage(List<Message> messageList, List<User> users, @NonNull ChatFun.Fun2<List<Message>, List<Message>> callback) {
        if (messageList.isEmpty()) return;
        for (User user : users) {
            List<Message> msgList = new ArrayList<>();
            for (int i = 0; i < messageList.size(); i++) {
                Message m = messageList.get(i);
                Message newMsg = Message.obtain(user, m.getConversationType(), m.getMessageType(), m.getMessageContent());
                newMsg.setCreateTime(ServeTime.currentTimeMillis() + i);
                newMsg.setReferMessage(m.getReferMessage());
                msgList.add(newMsg);
            }
            sendForwardMessage(msgList, new ArrayList<>(), new ArrayList<>(), callback);
        }
    }

    //发送->多条转发消息发送
    private static void sendForwardMessage(List<Message> messageList,
                                           List<Message> successMessage, List<Message> errorMessage,
                                           @NonNull ChatFun.Fun2<List<Message>, List<Message>> callback) {
        if (!messageList.isEmpty()) {
            Message message = messageList.get(0);
            sendMessage(message, null, new MessageCallback<>() {
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

    //删除消息
    public static void deleteMessage(Message message, @NonNull String toId, @Nullable MessageCallback<List<Message>> callback) {
        List<Message> messageList = new ArrayList<>();
        messageList.add(message);
        deleteMessage(messageList, toId, callback);
    }

    //删除消息
    public static void deleteMessage(List<Message> messageList, @NonNull String toId, @Nullable MessageCallback<List<Message>> callback) {
        ChatRepo.deleteMessages(messageList, toId, new FetchCallback<>() {
            @Override
            public void onError(int errorCode, @Nullable String errorMsg) {
                PostMessageEvent.postDeleteMessage(new DeleteMessageEvent(DeleteMessageEvent.ERROR, messageList));
                if (callback != null) {
                    callback.onError(messageList, errorCode);
                }
            }

            @Override
            public void onSuccess(@Nullable Integer data) {
                PostMessageEvent.postDeleteMessage(new DeleteMessageEvent(DeleteMessageEvent.SUCCESS, messageList));
                if (callback != null) {
                    callback.onSuccess(messageList);
                }
            }
        });
    }

    //发送语音消息
    public static void sendVoiceMessage(User toUser, ConversationType conversationType, AudioDataBean voiceData, @Nullable Message referMessage, @Nullable MessageCallback<Message> callback) {
        HQVoiceMessage voiceBody = HQVoiceMessage.obtain(voiceData.getUrl(), voiceData.getPath(), voiceData.getDuration());
        Message message = Message.obtain(toUser, conversationType, MessageType.CHAT_VOICE, voiceBody);
        PostMessageEvent.postSendMediaMessage(new ChatMessageEvent(ChatMessageEvent.PROGRESS, message, 0));
        sendMessage(message, referMessage, callback);
    }

//    //发送订单消息
//    public void sendOrderMessage(User user, Conversation.ConversationType type, OrderMessage body, MessageCallback callback) {
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
//    public void sendGoodsMessage(User user, Conversation.ConversationType type, GoodsMessage goodsMessage, MessageCallback callback) {
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

    //上传并发送消息
    public static void uploadAndSendMediaMessage(Message message, File file) {
        PostMessageEvent.postSendMediaMessage(new ChatMessageEvent(ChatMessageEvent.PROGRESS, message, 0));
        UploadDownloadProcessor uploadProcessor = IMCenter.getInstance().getOptions().uploadDownloadProcessor;
        if (uploadProcessor != null) {
            uploadProcessor.upload(file, url -> {
                MediaMessage body = (MediaMessage) message.getMessageContent();
                body.setUrl(url);
                message.setMessageBody(body.toJson());
                message.setMessageContent(body);
                sendMessage(message, null, false, false, new MessageCallback<>() {
                    @Override
                    public void onSuccess(Message message) {
                        message.setSendStatus(State.SUCCESS);
                        PostMessageEvent.postSendMediaMessage(new ChatMessageEvent(ChatMessageEvent.SUCCESS, message));
                    }

                    @Override
                    public void onError(Message message, int errorCode) {
                        message.setSendStatus(State.ERROR);
                        PostMessageEvent.postSendMediaMessage(new ChatMessageEvent(ChatMessageEvent.ERROR, message));
                    }
                });
            }, errorMessage -> {
                ResendManager.getInstance().addResendMessage(message, false);
                message.setSendStatus(State.ERROR);
                PostMessageEvent.postSendMediaMessage(new ChatMessageEvent(ChatMessageEvent.ERROR, message));
            }, progress -> {
                message.setSendStatus(State.PROGRESS);
                PostMessageEvent.postSendMediaMessage(new ChatMessageEvent(ChatMessageEvent.PROGRESS, message, progress.intValue()));
            });
        }
    }

}
