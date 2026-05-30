package io.im.uicommon.event;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.im.core.model.Message;


/**
 * 发送聊天消息事件
 */
public class ChatMessageEvent {
    @IntDef({SUCCESS, ATTACH, ERROR, PROGRESS, CANCEL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Event {
    }

    public static final int ATTACH = 0;
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    public static final int PROGRESS = 3;
    public static final int CANCEL = 4;

    private @Event int event;
    private Message message;
    private int code;
    private int progress;

    public ChatMessageEvent(@ChatMessageEvent.Event int event, Message message) {
        this(event, message, 0, -1);
    }

    public ChatMessageEvent(@ChatMessageEvent.Event int event, Message message, int progress) {
        this(event, message, progress, -1);
    }

    public ChatMessageEvent(@ChatMessageEvent.Event int event, int code, Message message) {
        this(event, message, 0, code);
    }

    public ChatMessageEvent(
            @ChatMessageEvent.Event int event, Message message, int progress, int code) {
        this.event = event;
        this.message = message;
        this.progress = progress;
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

    public int getProgress() {
        return progress;
    }
}
