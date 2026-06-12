package io.im.core.model;


import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import io.im.core.core.ChatSDK;
import io.im.core.utils.ChatNull;

/**
 * by DAD FZ
 * 2026/6/12
 * desc：撤回消息
 **/
@Keep
@Entity(tableName = "revoke_message")
public class ReMessage implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rid")
    private long rid;

    @ColumnInfo(name = "fromId")
    private String fromId;

    @ColumnInfo(name = "toId")
    private String toId;

    @ColumnInfo(name = "content")
    private String content;


    public long getRid() {
        return rid;
    }

    public void setRid(long rid) {
        this.rid = rid;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getContent() {
        return ChatNull.compat(content);
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Nullable
    public Message getMessage() {
        return Message.parseMessageFromJsonOrNull(getContent());
    }

    public static ReMessage obtain(String toId, @NonNull Message message) {
        ReMessage reMessage = new ReMessage();
        reMessage.setFromId(ChatSDK.getConnectUser().getId());
        reMessage.setToId(toId);
        reMessage.setContent(message.toJson());
        return reMessage;
    }
}
