package io.im.kit.chat;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import io.im.kit.IMCenter;
import io.im.kit.R;
import io.im.kit.databinding.KitActivityChatBinding;
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
public class IChatActivity extends ChatBaseActivity {
    private KitActivityChatBinding binding;

    private ChatFragmentPageAdapter adapter;

    private int uiMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = KitActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        List<ChatBaseFragment> fragments = new ArrayList<>();
        fragments.add(new IChatFragment());
        binding.pager.setAdapter(adapter = new ChatFragmentPageAdapter(getSupportFragmentManager(), fragments));
        UserInfo user = (UserInfo) getIntent().getSerializableExtra(RouteUtil.User);
        if (user != null) {
            binding.conversationToolbar.setTitleName(user.getUserName());
        }
        binding.conversationToolbar.setLeftOnclick(v -> onBackPressed());

//        StatusBarHelper.setStatusBarColor(this, Color.TRANSPARENT);

        SystemBarStyle statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT);
        SystemBarStyle navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT);
        EdgeToEdge.enable(this, statusBarStyle, navigationBarStyle);

        IMCenter.getInstance().getOptions().getFontSizeLiveData().observe(this, fontSize -> {
            if (!isFinishing() && fontSize != null) {
                binding.conversationToolbar.updateTitleSize();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
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
