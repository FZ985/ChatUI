package io.chat.kit.processor;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.chat.kit.repo.ChatRepo;
import io.im.core.listener.ChatFun;
import io.im.core.listener.FetchCallback;
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

    public void insertMessage(@NonNull Message message, @NonNull ChatFun.Fun1<Long> call) {
        ChatRepo.insertLocalMessage(message, new FetchCallback<Long>() {
            @Override
            public void onError(int errorCode, @Nullable String errorMsg) {

            }

            @Override
            public void onSuccess(@Nullable Long data) {
                if (data != null) {
                    call.apply(data);
                }
            }
        });
    }

    public void updateMessage(@NonNull Message message, @Nullable ChatFun.Fun call) {
        ChatRepo.updateLocalMessage(message, new FetchCallback<>() {
            @Override
            public void onError(int errorCode, @Nullable String errorMsg) {

            }

            @Override
            public void onSuccess(@Nullable Void data) {
                if (call != null) {
                    call.apply();
                }
            }
        });
    }

    public abstract void deleteMessage(@NonNull UserInfo user, @NonNull Message message);

    public abstract void deleteMessages(@NonNull UserInfo user, @NonNull List<Message> messages);

    public abstract void clearMessage(@NonNull UserInfo user);
}
