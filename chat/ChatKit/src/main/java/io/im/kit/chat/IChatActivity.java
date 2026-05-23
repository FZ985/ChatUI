package io.im.kit.chat;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.im.kit.IMCenter;
import io.im.kit.R;
import io.im.kit.databinding.ChatActivityChatBinding;
import io.im.kit.utils.RouteUtil;
import io.im.lib.base.ChatBaseActivity;
import io.im.lib.base.ChatBaseFragment;
import io.im.lib.base.ChatFragmentPageAdapter;
import io.im.lib.model.UserInfo;

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
        fragments.add(new IChatFragment());
        getBinding().pager.setAdapter(adapter = new ChatFragmentPageAdapter(getSupportFragmentManager(), fragments));
        UserInfo user = (UserInfo) getIntent().getSerializableExtra(RouteUtil.User);
        if (user != null) {
            getBinding().conversationToolbar.setTitleName(user.getUserName());
        }
        getBinding().conversationToolbar.setLeftOnclick(v -> onBackPressed());

//        StatusBarHelper.setStatusBarColor(this, Color.TRANSPARENT);

        IMCenter.getInstance().getOptions().getFontSizeLiveData().observe(this, fontSize -> {
            if (!isFinishing() && fontSize != null) {
                getBinding().conversationToolbar.updateTitleSize();
            }
        });

        uiMode = getResources().getConfiguration().uiMode;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int newUiMode = newConfig.uiMode;
        if (newUiMode != uiMode) {
//            StatusBarHelper.setStatusBarColor(this, Color.TRANSPARENT);
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
