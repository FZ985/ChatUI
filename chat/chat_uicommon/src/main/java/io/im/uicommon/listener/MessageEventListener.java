package io.im.uicommon.listener;

import io.im.uicommon.event.ChatMessageEvent;
import io.im.uicommon.event.DeleteMessageEvent;

public interface MessageEventListener {

    //发送消息回调
    default void onSendMessage(ChatMessageEvent event) {
    }

    //发送媒体消息回调
    default void onSendMediaMessage(ChatMessageEvent event) {
    }

    //发送的其他消息回调
    default void onSendOtherMessage(ChatMessageEvent event) {
    }

    //接收消息回调
    default void onReceiveMessage(ChatMessageEvent event) {
    }

    //接收的其他消息回调
    default void onReceiveOtherMessage(ChatMessageEvent event) {
    }

    //删除消息回调
    default void onDeleteMessage(DeleteMessageEvent event) {
    }

}
