package io.im.lib.model;

import android.text.TextUtils;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import io.im.lib.message.UnKnowMessage;
import io.im.lib.utils.ChatNull;
import io.im.lib.utils.JLog;

/**
 * author : JFZ
 * date : 2024/1/27 10:54
 * description :
 */
@Keep
@Entity(tableName = "message")
public class Message implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "messageId")
    private long messageId;

    @ColumnInfo(name = "messageTime")
    private long messageTime;

    @Embedded(prefix = "from_")
    private MessageUser fromUser;

    @Embedded(prefix = "to_")
    private MessageUser toUser;

    @ColumnInfo(name = "chatType")
    private int chatType;//聊天类型： 1:单聊 2：群聊

    @ColumnInfo(name = "messageType")
    private int messageType;//消息类型

    @ColumnInfo(name = "messageBody")
    private String messageBody;

    @ColumnInfo(name = "readStatus")
    private int readStatus;//阅读状态

    @ColumnInfo(name = "sendStatus")
    private int sendStatus;//发送、接收状态

    @Ignore
    private MessageDirection messageDirection;//消息方向， 左边、右边

    @Ignore
    private ConversationType conversationType;//会话类型， 单聊、群聊

    @Ignore
    private MessageContent messageContent;

    public Message() {
    }

    @Ignore
    public Message(MessageContent messageContent) {
        this.messageContent = messageContent;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageBody() {
        return ChatNull.compat(messageBody);
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public MessageContent getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(MessageContent messageContent) {
        this.messageContent = messageContent;
    }

    public MessageDirection getMessageDirection() {
        return messageDirection;
    }

    public void setMessageDirection(MessageDirection messageDirection) {
        this.messageDirection = messageDirection;
    }

    public MessageUser getFromUser() {
        if (fromUser == null) {
            fromUser = new MessageUser();
        }
        return fromUser;
    }

    public void setFromUser(MessageUser fromUser) {
        this.fromUser = fromUser;
    }

    public MessageUser getToUser() {
        if (toUser == null) {
            toUser = new MessageUser();
        }
        return toUser;
    }

    public void setToUser(MessageUser toUser) {
        this.toUser = toUser;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public ConversationType getConversationType() {
        if (conversationType == null) {
            conversationType = ConversationType.setValue(getChatType());
        }
        return conversationType;
    }

    public Message setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
        return this;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public ReadStatus getReadStatusEnum() {
        return ReadStatus.setValue(getReadStatus());
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public SentStatus getSendStatusEnum() {
        return SentStatus.setValue(getSendStatus());
    }

    public Message setReadStatus(ReadStatus readStatus) {
        setReadStatus(readStatus.getValue());
        return this;
    }

    public long buildMessageId() {
        return MessageIdGenerator.nextId();
    }

    public enum MessageDirection {
        SEND(1),
        RECEIVE(2);

        private final int value;

        private MessageDirection(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static MessageDirection setValue(int code) {
            MessageDirection[] var1 = values();
            for (MessageDirection c : var1) {
                if (code == c.getValue()) {
                    return c;
                }
            }
            return SEND;
        }
    }

    public enum SentStatus {
        FAILED(-1),//失败
        SENDING(0),//发送中
        SEND_SUCCESS(1),//发送成功
        RECEIVED(2);//接收

        private final int value;

        private SentStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static SentStatus setValue(int code) {
            SentStatus[] var1 = values();
            for (SentStatus c : var1) {
                if (code == c.getValue()) {
                    return c;
                }
            }
            return SENDING;
        }
    }


    public enum ReadStatus {
        UN_READ(0),//未读

        READ(1);//已读

        private final int value;

        private ReadStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static ReadStatus setValue(int code) {
            ReadStatus[] var1 = values();
            for (ReadStatus c : var1) {
                if (code == c.getValue()) {
                    return c;
                }
            }
            return READ;
        }
    }


    @NonNull
    public static Message parseMessageFromJson(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject obj = new JSONObject(json);
                long messageId = obj.optLong("messageId");
                String from = obj.optString("from");
                String from_n = obj.optString("from_n");
                String to = obj.optString("to");
                String to_n = obj.optString("to_n");
                String shopId = obj.optString("shopId");
                String from_avatar = obj.optString("from_avatar");
                String to_avatar = obj.optString("to_avatar");
                String os = obj.optString("os", "");
                int type = obj.optInt("type");
                int chatType = obj.optInt("chatType");
                int isRead = obj.optInt("isRead");
                int userType = obj.optInt("userType");
                int status = obj.optInt("status", SentStatus.SENDING.value);
                long sendTime = obj.optLong("sendTime");
                Object bodyObj = obj.opt("body");
                JSONObject body = null;
                if (bodyObj != null) {
                    if (bodyObj instanceof String) {
                        String bodyStr = bodyObj.toString();
                        if (bodyStr.startsWith("{") && bodyStr.endsWith("}")) {
                            body = new JSONObject(bodyStr);
                        }
                    } else if (bodyObj instanceof JSONObject) {
                        body = obj.optJSONObject("body");
                    }
                }
                String bodyJson = "{}";
                if (body != null) {
                    bodyJson = body.toString();
                }
                if (!TextUtils.isEmpty(from) && !TextUtils.isEmpty(to)) {
                    return new Message(new UnKnowMessage());
//                    return new Message(messageId, sendTime, from, from_n, to, to_n, type, userType, chatType, isRead, status, shopId, from_avatar, to_avatar, bodyJson, os).clone();
                }
            } catch (JSONException e) {
                JLog.e("解析消息失败:" + e.getMessage());
            }
        }
        return new Message(new UnKnowMessage());
    }
}
