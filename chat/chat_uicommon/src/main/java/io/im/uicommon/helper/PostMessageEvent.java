package io.im.uicommon.helper;


import java.util.List;

import io.im.uicommon.IMCenter;
import io.im.uicommon.listener.MessageEventListener;

/**
 * by DAD FZ
 * 2026/7/2
 * desc：
 **/
public class PostMessageEvent {

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
}
