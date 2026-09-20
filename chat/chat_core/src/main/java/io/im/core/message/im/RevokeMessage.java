package io.im.core.message.im;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.Serializable;

import io.im.core.model.Message;
import io.im.core.model.MessageContent;
import io.im.core.model.RoleType;
import io.im.core.model.User;
import io.im.core.utils.ChatNull;

/**
 * author : JFZ
 * date : 2024/1/27 13:31
 * description :
 */
@Keep
public final class RevokeMessage extends MessageContent implements Serializable {

    private String revokeUser;//谁撤回的，针对群聊或特殊权限人员可撤回
    private int role = -1;
    private String content;

    public static RevokeMessage obtain(RoleType role, User revokeUser, Message oldMessage) {
        RevokeMessage body = new RevokeMessage();
        body.setContent(oldMessage.toJson());
        body.setRevokeUser(revokeUser.toJson());
        body.setRole(role.getValue());
        return body;
    }

    @Override
    public MessageContent parseContent(JSONObject obj) {
        if (obj != null) {
            setContent(obj.optString("content"));
            setRevokeUser(obj.optString("revokeUser"));
            setRole(obj.optInt("role", -1));
        }
        return this;
    }

    public String getContent() {
        return ChatNull.compat(content);
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Nullable
    public Message getRevokeMessage() {
        return Message.parseMessageFromJsonOrNull(getContent());
    }

    @Override
    public Spannable getSummarySpannable(Context context) {
        return null;
    }

    public String getRevokeUser() {
        return revokeUser;
    }

    public void setRevokeUser(String revokeUser) {
        this.revokeUser = revokeUser;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Nullable
    public User getUser() {
        String revokeUserStr = getRevokeUser();
        if (!TextUtils.isEmpty(revokeUserStr)) {
            User user = User.fromJson(revokeUserStr);
            if (user.isValid()) {
                return user;
            }
        }
        return null;
    }

    public RoleType getRoleType() {
        return RoleType.setValue(getRole());
    }

}
