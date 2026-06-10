package io.im.core.model;

import android.text.TextUtils;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import io.im.core.utils.ChatNull;


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

    @ColumnInfo(name = "unreadCount")
    private int unreadCount;//未读数

    @ColumnInfo(name = "lastMessage")
    private String lastMessage;//最后一条消息

    @ColumnInfo(name = "isTop")
    private int isTop;//是否置顶

    @ColumnInfo(name = "isDisturb")
    private int isDisturb;//是否免打扰

    @Ignore
    private boolean isLocalChange;//数据变动，更新UI

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

    public UserInfo getUser() {
        return UserInfo.fromJson(getSession());
    }

    public String getFriendName() {
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

    public boolean isLocalChange() {
        return isLocalChange;
    }

    public void setLocalChange(boolean localChange) {
        isLocalChange = localChange;
    }

    public void change() {
        isLocalChange = true;
    }
}
