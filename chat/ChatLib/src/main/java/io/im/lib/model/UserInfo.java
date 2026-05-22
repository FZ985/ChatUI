package io.im.lib.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import io.im.lib.utils.ChatLibUtil;
import io.im.lib.utils.ChatNull;

/**
 * author : JFZ
 * date : 2024/1/26 16:34
 * description :
 */
@Keep
@Entity(tableName = "userInfo")
public class UserInfo implements Serializable {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "userId")
    private String userId;
    @ColumnInfo(name = "userName")
    private String userName;
    @ColumnInfo(name = "userAvatar")
    private String userAvatar;
    @ColumnInfo(name = "userType")
    private int userType;

    public UserInfo() {
        userId = "";
    }

    @Ignore
    public UserInfo(String userId, String userName, String userAvatar) {
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
    }

    public String getUserId() {
        return ChatNull.compat(userId);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return ChatNull.compat(userName);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return ChatNull.compat(userAvatar);
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public MessageUser toMessageUser() {
        String json = ChatLibUtil.toJson(this);
        return ChatLibUtil.gson.fromJson(json, MessageUser.class);
    }
}
