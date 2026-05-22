package io.im.kit.event.actionevent;


import io.im.lib.model.Message;

/**
 * 其他消息事件，非聊天消息事件
 */
public class OtherMessageEvent {

    private Message message;

    public OtherMessageEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
