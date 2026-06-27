package io.chat.kit.processor;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.im.core.listener.ChatFun;
import io.im.core.listener.FetchCallback;
import io.im.core.model.Message;
import io.im.core.model.User;
import io.im.uicommon.repo.ChatRepo;

/**
 * by DAD FZ
 * 2026/6/24
 * desc：
 **/
public abstract class ChatMessageProcessor {

    protected int mPageSize = 100;
    protected long lastMessageId;

    public abstract void getFirstMessage(@NonNull User user, @NonNull ChatFun.Fun1<List<Message>> call);

    public abstract void loadMoreMessage(@NonNull User user, @NonNull ChatFun.Fun1<List<Message>> call);

    public void insertMessage(@NonNull Message message, @NonNull ChatFun.Fun1<Long> call) {
        ChatRepo.insertLocalMessage(message, new FetchCallback<>() {
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

    public void deleteMessage(@NonNull User user, @NonNull Message message) {
        List<Message> list = new ArrayList<>();
        list.add(message);
        deleteMessages(user, list);
    }

    public void deleteMessages(@NonNull User user, @NonNull List<Message> messages) {
        ChatRepo.deleteMessages(
                messages,
                user.getId(),
                new FetchCallback<>() {
                    @Override
                    public void onError(int errorCode, @Nullable String errorMsg) {

                    }

                    @Override
                    public void onSuccess(@Nullable Integer data) {

                    }
                });
    }

    public abstract void clearMessage(@NonNull User user);
}
