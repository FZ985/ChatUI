package io.im.uicommon.event;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import io.im.core.model.Message;
import io.im.core.utils.ChatNull;


/**
 * 删除聊天消息事件
 */
public class DeleteMessageEvent {
    @IntDef({SUCCESS, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Event {
    }

    public static final int SUCCESS = 1;
    public static final int ERROR = 2;

    private final @Event int event;
    private final List<Message> messages;

    public DeleteMessageEvent(@DeleteMessageEvent.Event int event, Message message) {
        List<Message> list = new ArrayList<>();
        list.add(message);
        this.event = event;
        this.messages = list;
    }

    public DeleteMessageEvent(@DeleteMessageEvent.Event int event, List<Message> messages) {
        this.event = event;
        this.messages = messages;
    }

    public @Event int getEvent() {
        return event;
    }

    public List<Message> getMessages() {
        return ChatNull.compatList(messages);
    }

}
