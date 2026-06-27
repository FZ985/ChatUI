package io.chat.kit.model;


import java.io.Serializable;
import java.util.List;

import io.im.core.model.User;
import io.im.core.utils.ChatNull;

/**
 * by DAD FZ
 * 2026/5/29
 * desc：
 **/
public class ForwardSelectorBean implements Serializable {

    private final List<User> users;

    private final String messageContent;

    public ForwardSelectorBean(List<User> users, String messageContent) {
        this.users = users;
        this.messageContent = messageContent;
    }

    public List<User> getUsers() {
        return ChatNull.compatList(users);
    }

    public String getMessageContent() {
        return ChatNull.compat(messageContent);
    }
}
