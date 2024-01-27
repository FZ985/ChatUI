package io.im.lib.model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.Random;

import io.im.lib.utils.ChatNull;

/**
 * author : JFZ
 * date : 2024/1/27 10:54
 * description :
 */
@Keep
public class Message implements Serializable {

    private long messageId;

    private long messageTime;

    private String fromId;

    private String fromAvatar;

    private String fromName;

    private String toId;

    private String toAvatar;

    private String toName;

    private MessageContent messageContent;


    private MessageDirection messageDirection;//消息方向， 左边、右边

    private ConversationType conversationType;//会话类型， 单聊、群聊

    private SentStatus sendStatus;//发送、接收状态

    private ReadStatus readStatus;//阅读状态

    public Message() {
    }

    public Message(MessageContent messageContent) {
        this.messageContent = messageContent;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
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

    public String getFromId() {
        return ChatNull.compatValue(fromId);
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromAvatar() {
        return ChatNull.compatValue(fromAvatar);
    }

    public void setFromAvatar(String fromAvatar) {
        this.fromAvatar = fromAvatar;
    }

    public String getFromName() {
        return ChatNull.compatValue(fromName);
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToId() {
        return ChatNull.compatValue(toId);
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getToAvatar() {
        return ChatNull.compatValue(toAvatar);
    }

    public void setToAvatar(String toAvatar) {
        this.toAvatar = toAvatar;
    }

    public String getToName() {
        return ChatNull.compatValue(toName,"用户");
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public ConversationType getConversationType() {
        if (conversationType == null) {
            conversationType = ConversationType.PRIVATE;
        }
        return conversationType;
    }

    public Message setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
        return this;
    }

    public SentStatus getSendStatus() {
        if (sendStatus == null) {
            sendStatus = SentStatus.SEND_SUCCESS;
        }
        return sendStatus;
    }

    public Message setSendStatus(SentStatus sendStatus) {
        this.sendStatus = sendStatus;
        return this;
    }

    public ReadStatus getReadStatus() {
        if (readStatus == null) {
            readStatus = ReadStatus.READ;
        }
        return readStatus;
    }

    public Message setReadStatus(ReadStatus readStatus) {
        this.readStatus = readStatus;
        return this;
    }

    private long buildMessageId() {
        return System.currentTimeMillis() + (new Random().nextInt(999 - 10) + 10);
    }

    public enum MessageDirection {
        SEND(1),
        RECEIVE(2);

        private int value;

        private MessageDirection(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static MessageDirection setValue(int code) {
            MessageDirection[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                MessageDirection c = var1[var3];
                if (code == c.getValue()) {
                    return c;
                }
            }
            return SEND;
        }
    }

    public static enum SentStatus {
        SENDING(State.PROGRESS),//发送中

        SEND_SUCCESS(State.SUCCESS),//发送成功

        FAILED(State.ERROR),//失败

        RECEIVED(State.RECEIVED);//接收

        private final int value;

        private SentStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static SentStatus setValue(int code) {
            SentStatus[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                SentStatus c = var1[var3];
                if (code == c.getValue()) {
                    return c;
                }
            }
            return SENDING;
        }
    }


    public static enum ReadStatus {
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
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                ReadStatus c = var1[var3];
                if (code == c.getValue()) {
                    return c;
                }
            }
            return READ;
        }
    }
}
