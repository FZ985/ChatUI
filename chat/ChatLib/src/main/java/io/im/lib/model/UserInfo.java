package io.im.lib.model;

import androidx.annotation.Keep;

import java.io.Serializable;

import io.im.lib.utils.ChatNull;

/**
 * author : JFZ
 * date : 2024/1/26 16:34
 * description :
 */
@Keep
public class UserInfo implements Serializable {

    private String userId;
    private String userName;
    private String userAvatar;

    public UserInfo(String userId, String userName, String userAvatar) {
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
    }

    public String getUserId() {
        return ChatNull.compatValue(userId);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return ChatNull.compatValue(userName);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return ChatNull.compatValue(userAvatar);
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

}
