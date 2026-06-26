package io.im.core.model;

import android.text.TextUtils;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import io.im.core.utils.ChatLibUtil;
import io.im.core.utils.ChatNull;
import io.im.core.utils.ConversationIdUtil;
import io.im.core.utils.ServeTime;


/**
 * author : JFZ
 * date : 2026/6/10 15:13
 * description :
 */
@Keep
@Entity(tableName = "session")
public class Session implements Serializable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "sid")
    private String sid;

    @ColumnInfo(name = "session")
    private String session;//会话用户、群、服务号等等信息

    @ColumnInfo(name = "type")
    private int type;//会话类型，对应session中的type，及 ConversationType

    @ColumnInfo(name = "unreadCount")
    private int unreadCount;//未读数

    @ColumnInfo(name = "lastMessage")
    private String lastMessage;//最后一条消息

    @ColumnInfo(name = "isTop")
    private int isTop;//是否置顶 0:否 1:是

    @ColumnInfo(name = "isDisturb")
    private int isDisturb;//是否免打扰 0:否 1:是

    @ColumnInfo(name = "createTime")
    private long createTime;

    @ColumnInfo(name = "updateTime")
    private long updateTime;


    public Session() {

    }

    @NonNull
    public String getSid() {
        return sid;
    }

    public void setSid(@NonNull String sid) {
        this.sid = sid;
    }

    public String getSession() {
        return ChatNull.compat(session);
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Nullable
    public Message getLastMessageObj() {
        return Message.parseMessageFromJsonOrNull(getLastMessage());
    }

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public int getIsDisturb() {
        return isDisturb;
    }

    public void setIsDisturb(int isDisturb) {
        this.isDisturb = isDisturb;
    }

    public boolean isMute() {
        return getIsDisturb() == 1;
    }

    public UserInfo getUser() {
        return UserInfo.fromJson(getSession());
    }

    public String getName() {
        String defaultName = "";
        UserInfo user = getUser();
        if (!TextUtils.isEmpty(user.getId()) && user.getId().length() > 4) {
            defaultName = user.getId().substring(user.getId().length() - 4);
        }
        if (TextUtils.isEmpty(user.getName())) {
            return "用户" + defaultName;
        }
        return user.getName();
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return ChatLibUtil.toJson(this);
    }

    public static Session obtain(UserInfo user, ConversationType conversationType, Message message) {
        Session session = new Session();
        session.setCreateTime(ServeTime.currentTimeMillis());
        session.setSid(ConversationIdUtil.conversationId(user.getId(), conversationType));
        session.setSession(user.toJson());
        session.setType(conversationType.getValue());
        session.setLastMessage(message.toJson());
        return session;
    }
}
