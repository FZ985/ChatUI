package io.im.lib.model;

import android.text.TextUtils;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import io.im.lib.utils.ChatNull;


/**
 * author : JFZ
 * date : 2024/1/3 13:40
 * description :
 */
@Keep
@Entity(tableName = "session")
public class Session implements Serializable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "userId")
    private String userId;

    @ColumnInfo(name = "sessionType")
    private int sessionType;

    @ColumnInfo(name = "friendId")
    private String friendId;

    @ColumnInfo(name = "friendAvatar")
    private String friendAvatar;

    @ColumnInfo(name = "friendName")
    private String friendName;

    @ColumnInfo(name = "noReadNumber")
    private int noReadNumber;

    @ColumnInfo(name = "msgType")
    private int msgType;

    @ColumnInfo(name = "newMessage")
    private String newMessage;

    @ColumnInfo(name = "createTime")
    private String createTime;

    @ColumnInfo(name = "updateTime")
    private String updateTime;

    @ColumnInfo(name = "newTime")
    private long newTime;

    @ColumnInfo(name = "shopId")
    private String shopId;

    @ColumnInfo(name = "topNum")
    private int topNum;

    public Session() {

    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getSessionType() {
        return sessionType;
    }

    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }

    public String getFriendId() {
        return ChatNull.compat(friendId);
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendAvatar() {
        return ChatNull.compat(friendAvatar);
    }

    public void setFriendAvatar(String friendAvatar) {
        this.friendAvatar = friendAvatar;
    }

    public String getFriendName() {
        String defaultName = "";
        if (!TextUtils.isEmpty(friendId) && friendId.length() > 4) {
            defaultName = friendId.substring(friendId.length() - 4);
        }
        if (TextUtils.isEmpty(friendName)) {
            return "用户" + defaultName;
        }
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public int getNoReadNumber() {
        return noReadNumber;
    }

    public void setNoReadNumber(int noReadNumber) {
        this.noReadNumber = noReadNumber;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getNewMessage() {
        return ChatNull.compat(newMessage);
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public long getNewTime() {
        return newTime;
    }

    public void setNewTime(long newTime) {
        this.newTime = newTime;
    }

    public String getShopId() {
        return ChatNull.compat(shopId);
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public int getTopNum() {
        return topNum;
    }

    public void setTopNum(int topNum) {
        this.topNum = topNum;
    }
}
