package io.im.kit.listener;

import io.im.kit.event.actionevent.ChatMessageEvent;

public interface MessageEventListener {

    //发送消息回调
    void onSendMessage(ChatMessageEvent event);

    //发送媒体消息回调
    void onSendMediaMessage(ChatMessageEvent event);

    //发送的其他消息回调
    void onSendOtherMessage(ChatMessageEvent event);

    //接收消息回调
    void onReceiveMessage(ChatMessageEvent event);

    //接收的其他消息回调
    void onReceiveOtherMessage(ChatMessageEvent event);

}
