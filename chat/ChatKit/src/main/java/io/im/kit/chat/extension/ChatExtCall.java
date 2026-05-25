package io.im.kit.chat.extension;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.im.kit.helper.IChatHelper;
import io.im.lib.model.ConversationType;
import io.im.lib.model.UserInfo;

/**
 * author : JFZ
 * date : 2024/1/31 13:11
 * description :
 */
public interface ChatExtCall {

    void updateUser(UserInfo user);

    UserInfo getUser();

    ConversationType getConversationType();

    int addHeadView(@NonNull View view);

    int addFootView(@NonNull View view);

    void removeHeadView(int viewType);

    void removeFootView(int viewType);

    AppCompatActivity getConversationActivity();

    Intent getConversationIntent();

    ChatMessageViewModel getChatMessageViewModel();

    IChatHelper getIChatHelper();
}
