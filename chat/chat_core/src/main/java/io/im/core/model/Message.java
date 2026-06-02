package io.im.core.model;

import android.text.TextUtils;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import io.im.core.MessageType;
import io.im.core.core.ChatSDK;
import io.im.core.message.UnKnowMessage;
import io.im.core.utils.ChatLibUtil;
import io.im.core.utils.ChatNull;
import io.im.core.utils.JLog;

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
    private String messageBody;//消息内容

    @ColumnInfo(name = "replyMessage")
    private String replyMessage;//引用的消息，Message结构

    @ColumnInfo(name = "readStatus")
    private int readStatus;//阅读状态

    @ColumnInfo(name = "sendStatus")
    private int sendStatus;//发送状态

    @Ignore
    private MessageDirection messageDirection;//消息方向， 左边、右边

    @Ignore
    private ConversationType conversationType;//会话类型， 单聊、群聊

    @Ignore
    private MessageContent messageContent;

    @Ignore
    private Message innerReplyMessage;//内部引用消息的对象转换

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
        parseBody();
    }

    public void updateMessageBody(MessageContent messageContent) {
        this.messageBody = messageContent.toJson();
        this.messageContent = null;
        parseBody();
    }

    private void parseBody() {
        if (messageContent == null) {
            try {
                JSONObject bodyObj = new JSONObject(getMessageBody());
                MessageContent content;
                if (MessageType.isAppType(getMessageType())) {
                    content = MessageType.getAppMessageContent(getMessageType());
                } else {
                    content = MessageType.getMessageContent(getMessageType());
                }
                content.parseJsonToContent(bodyObj);
                setMessageContent(content);
            } catch (JSONException e) {
                setMessageContent(new UnKnowMessage());
            }
        }
    }

    public String getReplyMessage() {
        return ChatNull.compat(replyMessage);
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    @Nullable
    public Message getInnerReplyMessage() {
        if (innerReplyMessage == null && !TextUtils.isEmpty(getReplyMessage())) {
            innerReplyMessage = Message.parseMessageFromJson(getReplyMessage());
        }
        return innerReplyMessage;
    }

    public MessageContent getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(MessageContent messageContent) {
        this.messageContent = messageContent;
    }

    public MessageDirection getMessageDirection() {
        if (messageDirection == null) {
            if (ChatSDK.getConnectUser().getUserId().equals(getFromUser().getUserId())) {
                setMessageDirection(MessageDirection.SEND);
            } else {
                setMessageDirection(MessageDirection.RECEIVE);
            }
        }
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

    public Message setReadStatusEnum(ReadStatus readStatus) {
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

    //发送消息创建的消息体
    public static Message obtain(UserInfo toUser, ConversationType chatType, int messageType, MessageContent body) {
        Message message = new Message();
        message.setMessageId(message.buildMessageId());
        message.setMessageTime(System.currentTimeMillis());
        message.setFromUser(ChatSDK.getConnectUser().toMessageUser());
        message.setToUser(toUser.toMessageUser());
        message.setChatType(chatType.getValue());
        message.setMessageType(messageType);
        message.setMessageBody(body.toJson());
        message.setReadStatus(ReadStatus.UN_READ.getValue());
        message.setSendStatus(SentStatus.SENDING.getValue());
        return message;
    }


    //消息方向反转
    public Message flipFromTo() {
        MessageUser tempFrom = getFromUser();
        setFromUser(getToUser());
        setToUser(tempFrom);
        setMessageDirection(MessageDirection.RECEIVE);
        return this;
    }

    @NonNull
    public static Message parseMessageFromJson(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                Message message = new Message();
                JSONObject obj = new JSONObject(json);

                long messageId = obj.optLong("messageId");
                message.setMessageId(messageId);

                long messageTime = obj.optLong("messageTime");
                message.setMessageTime(messageTime);

                JSONObject fromUserObj = obj.optJSONObject("fromUser");
                message.setFromUser(MessageUser.fromJSONObject(fromUserObj));

                JSONObject toUserObj = obj.optJSONObject("toUser");
                message.setToUser(MessageUser.fromJSONObject(toUserObj));

                message.setChatType(obj.optInt("chatType", ConversationType.PRIVATE.getValue()));

                message.setMessageType(obj.optInt("messageType"));

                message.setMessageBody(obj.optString("messageBody", "{}"));

                message.setReadStatus(obj.optInt("readStatus", ReadStatus.UN_READ.getValue()));

                message.setSendStatus(obj.optInt("sendStatus", SentStatus.SENDING.getValue()));

                message.setReplyMessage(obj.optString("replyMessage", ""));

                return message;
            } catch (JSONException e) {
                JLog.e("解析消息失败:" + e.getMessage());
            }
        }
        return new Message(new UnKnowMessage());
    }


    public String toJson() {
        return ChatLibUtil.toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o instanceof Message) {
            Message that = (Message) o;
            return getMessageId() == that.getMessageId()
                    && getMessageTime() == that.getMessageTime()
                    && getMessageType() == that.getMessageType()
                    && getChatType() == that.getChatType();
        }
        return false;
    }
}
