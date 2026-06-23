package io.chat.conversation.model;


import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

import io.chat.kit.model.UiBaseBean;
import io.im.core.model.Session;
import io.im.core.model.UserInfo;
import io.im.core.utils.ConversationIdUtil;

/**
 * by DAD FZ
 * 2026/6/23
 * desc：
 **/
public class UiSession extends UiBaseBean implements Serializable {

    @NonNull
    private Session session;

    private Boolean isTop;//是否置顶

    private String targetId = "";


    public UiSession(@NonNull Session session) {
        this.session = session;
    }

    @NonNull
    public Session getSession() {
        return session;
    }

    public void setSession(@NonNull Session session) {
        this.session = session;
        change();
    }

    public Boolean isTop() {
        if (isTop == null) {
            isTop = session.getIsTop() == 1;
        }
        return isTop;
    }


    /**
     * 获取会话的最后一条消息时间
     *
     * @return 最后一条消息时间
     */
    public long getLastMsgTime() {
        if (session.getLastMessageObj() == null) {
            return session.getCreateTime();
        }
        return session.getLastMessageObj().getCreateTime();
    }

    public String getConversationName() {
        if (session != null) {
            return session.getName();
        }
        return getTargetId();
    }

    /**
     * 获取会话的目标ID
     *
     * @return 会话目标ID，P2P返回对方的ID，群组返回群ID
     */
    public String getTargetId() {
        if (TextUtils.isEmpty(targetId)) {
            targetId = ConversationIdUtil.conversationToAccountId(session.getSid());
        }
        return targetId;
    }

    public UserInfo getUser() {
        return session.getUser();
    }


    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof UiSession)) {
            return false;
        } else {
            UiSession that = (UiSession) o;
            return Objects.equals(session.getSid(), that.session.getSid());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(session.getSid());
    }

}
