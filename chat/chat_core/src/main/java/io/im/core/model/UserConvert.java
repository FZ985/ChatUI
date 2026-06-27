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
 * UserInfo的转换类，
 */
@Keep
public class UserConvert implements Serializable {
    private String id;
    private String name;
    private String avatar;
    private int type;
    private String remark;

    public UserConvert() {
    }

    @Ignore
    public UserConvert(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    public String getId() {
        return ChatNull.compat(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return ChatNull.compat(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return ChatNull.compat(avatar);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRemark() {
        return ChatNull.compat(remark);
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public User toUserInfo() {
        String json = toJson();
        return ChatLibUtil.gson.fromJson(json, User.class);
    }

    public String toJson() {
        return ChatLibUtil.toJson(this);
    }

    public static UserConvert fromJSONObject(@Nullable JSONObject obj) {
        UserConvert user = new UserConvert();
        if (obj != null) {
            user.setId(obj.optString("id"));
            user.setName(obj.optString("name"));
            user.setAvatar(obj.optString("avatar"));
            user.setType(obj.optInt("type"));
            user.setRemark(obj.optString("remark"));
        }
        return user;
    }

    public static UserConvert fromJson(String json) {
        try {
            return fromJSONObject(new JSONObject(json));
        } catch (JSONException e) {
            return new UserConvert();
        }
    }

}
