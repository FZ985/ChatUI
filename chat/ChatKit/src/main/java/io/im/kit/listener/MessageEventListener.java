package io.im.kit.listener;

import io.im.kit.event.actionevent.OtherMessageEvent;
import io.im.kit.event.actionevent.ReceiveMessageEvent;
import io.im.kit.event.actionevent.SendMediaMessageEvent;
import io.im.kit.event.actionevent.SendMessageEvent;

public interface MessageEventListener {

    //发送消息回调
    void onSendMessage(SendMessageEvent event);

    //发送媒体消息回调
    void onSendMediaMessage(SendMediaMessageEvent event);

    //发送的其他消息回调
    void onSendOtherMessage(OtherMessageEvent event);

    //接收消息回调
    void onReceiveMessage(ReceiveMessageEvent event);

    //接收的其他消息回调
    void onReceiveOtherMessage(OtherMessageEvent event);

}
