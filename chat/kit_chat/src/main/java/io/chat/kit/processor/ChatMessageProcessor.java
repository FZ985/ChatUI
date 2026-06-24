package io.chat.kit.processor;


import androidx.annotation.NonNull;

import java.util.List;

import io.im.core.listener.ChatFun;
import io.im.core.model.Message;
import io.im.core.model.UserInfo;

/**
 * by DAD FZ
 * 2026/6/24
 * desc：
 **/
public abstract class ChatMessageProcessor {

    protected int mPageSize = 100;
    protected long lastMessageId;

    public abstract void getFirstMessage(@NonNull UserInfo user, @NonNull ChatFun.Fun1<List<Message>> call);

    public abstract void loadMoreMessage(@NonNull UserInfo user, @NonNull ChatFun.Fun1<List<Message>> call);

    public abstract void insertMessage(@NonNull Message message, @NonNull ChatFun.Fun1<Long> call);

    public abstract void updateMessage(@NonNull Message message, ChatFun.Fun call);

    public abstract void deleteMessage(@NonNull UserInfo user, @NonNull Message message);

    public abstract void deleteMessages(@NonNull UserInfo user, @NonNull List<Message> messages);

    public abstract void clearMessage(@NonNull UserInfo user);
}
