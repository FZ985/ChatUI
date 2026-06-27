package io.chat.kit.chat.extension;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import io.chat.kit.chat.messagelist.viewmodel.ChatMessageViewModel;
import io.chat.kit.helper.IChatHelper;
import io.im.core.model.ConversationType;
import io.im.core.model.User;

/**
 * author : JFZ
 * date : 2024/1/31 13:11
 * description :
 */
public interface ChatExtCall {

    void updateUser(User user);

    User getUser();

    ConversationType getConversationType();

    int addHeadView(@NonNull View view);

    int addFootView(@NonNull View view);

    void removeHeadView(int viewType);

    void removeFootView(int viewType);

    AppCompatActivity getConversationActivity();

    Intent getConversationIntent();

    ChatMessageViewModel getChatMessageViewModel();

    IChatHelper getIChatHelper();

    void checkMultiSelectView();

    @NonNull
    LifecycleOwner getLifecycleOwner();

}
