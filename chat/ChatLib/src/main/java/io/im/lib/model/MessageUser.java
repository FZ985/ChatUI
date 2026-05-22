package io.im.lib.model;

import androidx.annotation.Keep;
import androidx.room.Ignore;

import java.io.Serializable;

import io.im.lib.utils.ChatLibUtil;
import io.im.lib.utils.ChatNull;

/**
 * author : JFZ
 * date : 2024/1/26 16:34
 * description :
 */
@Keep
public class MessageUser implements Serializable {
    private String userId;
    private String userName;
    private String userAvatar;
    private int userType;

    public MessageUser() {
    }

    @Ignore
    public MessageUser(String userId, String userName, String userAvatar) {
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

    public UserInfo toUserInfo() {
        String json = ChatLibUtil.toJson(this);
        return ChatLibUtil.gson.fromJson(json, UserInfo.class);
    }
}
