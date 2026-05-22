package io.im.kit;


import androidx.annotation.Nullable;

import io.im.kit.manager.MessageManager;
import io.im.lib.callback.SendMessageCallback;
import io.im.lib.model.Message;

/**
 * by DAD FZ
 * 2026/5/22
 * desc：消息创建
 **/
public class MessageCreate {


    //发送消息
    public static void sendMessage(Message message,@Nullable SendMessageCallback callback) {
        MessageManager.getInstance().sendMessage(message, callback);
    }

    //    //发送阅读消息
//    public void sendReadMessage(Message message, SendMessageCallback callback) {
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
//    public void sendRevokeMessage(Message message, SendMessageCallback callback) {
//        message.setType(MessageType.REVOKE_705);
//        sendMessage(message, callback);
//    }
//
//
//    //发送订单消息
//    public void sendOrderMessage(UserInfo user, Conversation.ConversationType type, OrderMessage body, SendMessageCallback callback) {
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
//    public void sendGoodsMessage(UserInfo user, Conversation.ConversationType type, GoodsMessage goodsMessage, SendMessageCallback callback) {
//        Message message = Message.obtain(user, type, MessageType.GOODS, goodsMessage);
//        List<MessageEventListener> listeners = IMCenter.getInstance().getOptions().getMessageEventListeners();
//        for (MessageEventListener listener : listeners) {
//            listener.onSendMessage(new SendEvent(SendEvent.ATTACH, message));
//        }
//        sendMessage(message, new SendMessageCallback() {
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
//                MessageManager.getInstance().sendMessage(message, new SendMessageCallback() {
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
//                MessageManager.getInstance().sendMessage(message, new SendMessageCallback() {
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
}
