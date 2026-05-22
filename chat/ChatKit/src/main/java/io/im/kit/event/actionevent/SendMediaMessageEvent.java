package io.im.kit.event.actionevent;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.im.lib.model.Message;


public class SendMediaMessageEvent {
    @IntDef({ATTACH, SUCCESS, PROGRESS, ERROR, CANCEL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Event {
    }

    public static final int ATTACH = 0;
    public static final int SUCCESS = 1;
    public static final int PROGRESS = 2;
    public static final int ERROR = 3;
    public static final int CANCEL = 4;

    private @Event int event;
    private Message message;
    private int code;
    private int progress;

    public SendMediaMessageEvent(@Event int event, Message message) {
        this(event, message, 0, -1);
    }

    public SendMediaMessageEvent(@Event int event, Message message, int progress) {
        this(event, message, progress, -1);
    }

    public SendMediaMessageEvent(@Event int event, int code, Message message) {
        this(event, message, 0, code);
    }

    public SendMediaMessageEvent(
            @Event int event, Message message, int progress, int code) {
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
