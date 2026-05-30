package io.im.core.model;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.room.Ignore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import io.im.core.utils.ChatLibUtil;
import io.im.core.utils.ChatNull;

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

    public static MessageUser fromJSONObject(@Nullable JSONObject obj) {
        MessageUser user = new MessageUser();
        if (obj != null) {
            user.setUserId(obj.optString("userId"));
            user.setUserName(obj.optString("userName"));
            user.setUserAvatar(obj.optString("userAvatar"));
            user.setUserType(obj.optInt("userType"));
        }
        return user;
    }

    public static MessageUser fromJson(String json) {
        try {
            return fromJSONObject(new JSONObject(json));
        } catch (JSONException e) {
            return new MessageUser();
        }
    }

}
