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
@Entity(tableName = "userInfo")
public class UserInfo implements Serializable {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "avatar")
    private String avatar;
    @ColumnInfo(name = "type")
    private int type;//用于区分用户、群、服务号、等等类型

    public UserInfo() {
        id = "";
    }

    @Ignore
    public UserInfo(@NonNull String id, String name, String avatar) {
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

    public MessageUser toMessageUser() {
        String json = ChatLibUtil.toJson(this);
        return ChatLibUtil.gson.fromJson(json, MessageUser.class);
    }


    public static UserInfo fromJSONObject(@Nullable JSONObject obj) {
        UserInfo user = new UserInfo();
        if (obj != null) {
            user.setId(obj.optString("id"));
            user.setName(obj.optString("name"));
            user.setAvatar(obj.optString("avatar"));
            user.setType(obj.optInt("type"));
        }
        return user;
    }

    public static UserInfo fromJson(String json) {
        try {
            return fromJSONObject(new JSONObject(json));
        } catch (JSONException e) {
            return new UserInfo();
        }
    }
}
