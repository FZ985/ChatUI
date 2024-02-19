package io.im.kit.conversation;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.im.kit.IMCenter;
import io.im.kit.R;
import io.im.kit.databinding.KitActivityConversationBinding;
import io.im.kit.utils.RouteUtil;
import io.im.kit.widget.immbar.ImmersionBar;
import io.im.lib.base.ChatBaseActivity;
import io.im.lib.base.ChatBaseFragment;
import io.im.lib.base.ChatFragmentPageAdapter;
import io.im.lib.model.UserInfo;

/**
 * author : JFZ
 * date : 2024/1/30 11:45
 * description :
 */
public class IConversationActivity extends ChatBaseActivity {
    private KitActivityConversationBinding binding;

    private ChatFragmentPageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = KitActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        List<ChatBaseFragment> fragments = new ArrayList<>();
        fragments.add(new IConversationFragment());
        binding.pager.setAdapter(adapter = new ChatFragmentPageAdapter(getSupportFragmentManager(), fragments));
        UserInfo user = (UserInfo) getIntent().getSerializableExtra(RouteUtil.User);
        if (user != null) {
            binding.conversationToolbar.setTitleName(user.getUserName());
        }
        binding.conversationToolbar.setLeftOnclick(v -> onBackPressed());
        ImmersionBar.with(this)
                .navigationBarColor(R.color.chat_conversation_bottom_panel)
                .init();

        IMCenter.getInstance().getOptions().getFontSizeLiveData().observe(this, fontSize -> {
            if (!isFinishing() && fontSize != null) {
                binding.conversationToolbar.updateTitleSize();
            }
        });
    }

    @Override
    public void onBackPressed() {
        ChatBaseFragment item = adapter.getItem(binding.pager.getCurrentItem());
        boolean pressed = item.onBackPressed();
        if (!pressed) {
            super.onBackPressed();
        }
    }

    @Override
    public int getChatActivityTheme() {
        return R.style.Kit_Conversation_Theme;
    }
}
