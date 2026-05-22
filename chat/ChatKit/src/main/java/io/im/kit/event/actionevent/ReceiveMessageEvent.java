package io.im.kit.event.actionevent;


import io.im.lib.model.Message;

/**
 * 接收聊天消息事件
 */
public class ReceiveMessageEvent {

    private Message message;

    public ReceiveMessageEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
