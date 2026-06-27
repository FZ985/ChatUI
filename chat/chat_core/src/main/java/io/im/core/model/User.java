package io.im.core.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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
@Entity(tableName = "user")
public class User implements Serializable {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "avatar")
    private String avatar;
    @ColumnInfo(name = "type")
    private int type;//用于区分用户、群、服务号、等等类型,对应ConversationType
    @ColumnInfo(name = "remark")
    private String remark;//备注

    public User() {
        id = "";
    }

    @Ignore
    public User(@NonNull String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    public String getId() {
        return ChatNull.compat(id);
    }

    public void setId(@NonNull String id) {
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

    public UserConvert toConvert() {
        String json = toJson();
        return ChatLibUtil.gson.fromJson(json, UserConvert.class);
    }

    public static User fromJSONObject(@Nullable JSONObject obj) {
        User user = new User();
        if (obj != null) {
            user.setId(obj.optString("id"));
            user.setName(obj.optString("name"));
            user.setAvatar(obj.optString("avatar"));
            user.setType(obj.optInt("type"));
            user.setRemark(obj.optString("remark"));
        }
        return user;
    }

    public static User fromJson(String json) {
        try {
            return fromJSONObject(new JSONObject(json));
        } catch (JSONException e) {
            return new User();
        }
    }

    public String toJson() {
        return ChatLibUtil.toJson(this);
    }
}
