package io.chat.kit.chat;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.chat.kit.R;
import io.chat.kit.databinding.ChatActivityChatBinding;
import io.im.uicommon.base.ChatBaseActivity;
import io.im.uicommon.base.ChatBaseFragment;
import io.im.uicommon.base.ChatFragmentPageAdapter;

/**
 * author : JFZ
 * date : 2024/1/30 11:45
 * description :
 */
public class IChatActivity extends ChatBaseActivity<ChatActivityChatBinding> {

    private ChatFragmentPageAdapter adapter;

    private int uiMode;


    @Override
    public void onInitPage(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        List<ChatBaseFragment> fragments = new ArrayList<>();
        IChatFragment chatFragment = new IChatFragment();
        fragments.add(chatFragment);
        getBinding().pager.setAdapter(adapter = new ChatFragmentPageAdapter(getSupportFragmentManager(), fragments));

        uiMode = getResources().getConfiguration().uiMode;

        chatFragment.onLoaded = () -> {
//            UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra(ChatRoute.User);
//            MessageOperate.sendMessage(IMTest.message(userInfo).get(10).getMessage(), null, null);
//            MessageOperate.sendMessage(IMTest.message(userInfo).get(1).getMessage(), null, null);
//            MessageOperate.sendMessage(IMTest.message(userInfo).get(2).getMessage(), null, null);
        };
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int newUiMode = newConfig.uiMode;
        if (newUiMode != uiMode) {
            uiMode = newUiMode;
            recreate();
        }
    }

    @Override
    public void onBackPressed() {
        ChatBaseFragment item = adapter.getItem(getBinding().pager.getCurrentItem());
        boolean pressed = item.onBackPressed();
        if (!pressed) {
            super.onBackPressed();
        }
    }

    @Override
    public int getChatActivityTheme() {
        return R.style.Kit_Conversation_Theme;
    }

    @NonNull
    @Override
    public ChatActivityChatBinding getBinding(@NotNull LayoutInflater inflater) {
        return ChatActivityChatBinding.inflate(inflater);
    }
}
