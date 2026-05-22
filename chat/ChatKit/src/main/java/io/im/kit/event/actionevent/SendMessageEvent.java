package io.im.kit.event.actionevent;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.im.lib.model.Message;


/**
 * 发送聊天消息事件
 */
public class SendMessageEvent {
    @IntDef({SUCCESS, ATTACH, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Event {
    }

    public static final int ATTACH = 0;
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;

    private @Event int event;
    private Message message;
    private int code;

    public SendMessageEvent(@Event int event, Message message) {
        this(event, message, -1);
    }

    public SendMessageEvent(int event, Message message, int code) {
        this.event = event;
        this.message = message;
        this.code = code;
    }

    public @Event int getEvent() {
        return event;
    }

    public Message getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
