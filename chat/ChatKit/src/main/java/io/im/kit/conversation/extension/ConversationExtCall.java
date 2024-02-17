package io.im.kit.conversation.extension;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.im.lib.model.ConversationType;
import io.im.lib.model.UserInfo;

/**
 * author : JFZ
 * date : 2024/1/31 13:11
 * description :
 */
public interface ConversationExtCall {

    void updateUser(UserInfo user);

    UserInfo getUser();

    ConversationType getConversationType();

    int addHeadView(@NonNull View view);

    int addFootView(@NonNull View view);

    void removeHeadView(int viewType);

    void removeFootView(int viewType);

    AppCompatActivity getConversationActivity();

    Intent getConversationIntent();

}
