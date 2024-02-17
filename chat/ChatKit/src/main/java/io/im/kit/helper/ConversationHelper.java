package io.im.kit.helper;

import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import io.im.kit.R;
import io.im.kit.config.ChatInputMode;
import io.im.kit.config.InputStyle;
import io.im.kit.conversation.IConversationFragment;
import io.im.kit.conversation.extension.ChatExtensionViewModel;
import io.im.kit.utils.RouteUtil;
import io.im.kit.widget.switchpanel.PanelSwitchHelper;
import io.im.kit.widget.switchpanel.interfaces.listener.OnViewClickListener;
import io.im.lib.callback.ChatLifecycle;
import io.im.lib.utils.JLog;

/**
 * author : JFZ
 * date : 2024/1/30 17:09
 * description :
 */
public final class ConversationHelper implements ChatLifecycle, OnViewClickListener {

    private static final String TAG = "ConversationHelper";
    private PanelSwitchHelper mHelper;

    private IConversationFragment mFragment;

    private Context mContext;

    private ChatExtensionViewModel mExtensionViewModel;

    public void bindConversation(Context context, IConversationFragment fragment) {
        if (fragment == null || context == null) return;
        this.mContext = context;
        this.mFragment = fragment;
        this.mFragment.getBinding().inputPanel.setInputStyle(InputStyle.setType(
                fragment.requireActivity().getIntent().getIntExtra(RouteUtil.InputStyle, InputStyle.All.getType())));
        mExtensionViewModel = new ViewModelProvider(fragment).get(ChatExtensionViewModel.class);
        mExtensionViewModel.getInputModeLiveData().observe(fragment, mode -> fragment.getBinding().inputPanel.updateInputMode(mode));
        mExtensionViewModel.setAttachChat(fragment, fragment.getBinding().inputPanel.getEditText());
        fragment.getBinding().inputPanel.getBinding().send.setOnClickListener(this::onClickBefore);
        fragment.getBinding().inputPanel.getBinding().voice.setOnClickListener(this::onClickBefore);
    }

    @Override
    public void onResume() {
        if (mFragment == null) {
            return;
        }
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(mFragment)
                    .setWindowInsetsRootView(mFragment.getBinding().getRoot())
                    .addKeyboardStateListener((visible, height) -> log("系统键盘是否可见 : " + visible + " 高度为：" + height))
                    .addEditTextFocusChangeListener((view, hasFocus) -> {
                        log("输入框是否获得焦点 : " + hasFocus);
                        if (hasFocus) {
                            if (mExtensionViewModel != null
                                    && mExtensionViewModel.getInputModeLiveData() != null) {
                                mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.TextInput);
                            }
                            scrollToBottom();
                        }
                        mFragment.getBinding().inputPanel.onEditTextFocus(hasFocus);
                    })
                    .addViewClickListener(ConversationHelper.this)
                    .build();
        }
        mFragment.getBinding().recycler.setPanelSwitchHelper(mHelper);
    }

    private void scrollToBottom() {
        if (mFragment != null) {
            mFragment.scrollToBottom();
        }
    }

    @Override
    public void onClickBefore(@Nullable View view) {
        if (view != null && mFragment != null) {
            int id = view.getId();
            if (id == R.id.voice) {
                if (mFragment.getBinding().inputPanel.isVoiceInputMode()) {
                    mFragment.getBinding().inputPanel.setIsVoiceInputMode(false);
                    mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.TextInput);
                    // 切换到文本输入模式后需要弹出软键盘
                    view.postDelayed(() -> mFragment.getBinding().inputPanel.getEditText().requestFocus(), 150);
                } else {
                    closeExpand();
                    mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.VoiceInput);
                    mFragment.getBinding().inputPanel.setIsVoiceInputMode(true);
                }
            } else if (id == R.id.emoji) {
                if (mExtensionViewModel.getInputModeLiveData().getValue() != null
                        && mExtensionViewModel.getInputModeLiveData().getValue() == ChatInputMode.EmoticonMode) {
                    mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.TextInput);
                } else {
                    mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.EmoticonMode);
                }
            } else if (id == R.id.add) {
                if (mExtensionViewModel.getInputModeLiveData().getValue() != null
                        && mExtensionViewModel.getInputModeLiveData().getValue() == ChatInputMode.PluginMode
                ) {
                    mExtensionViewModel.getInputModeLiveData().setValue(ChatInputMode.TextInput);
                } else {
                    mExtensionViewModel.getInputModeLiveData().setValue(ChatInputMode.PluginMode);
                }
            } else if (id == R.id.send) {
                mExtensionViewModel.onSendClick();
            }
            log("点击了View : " + view);
        }
    }

    @Override
    public void onPause() {
        if (mFragment != null) {
            mFragment.getBinding().switchLayout.recycle();
            mHelper = null;
        }
    }

    public void closeExpand() {
        mHelper.hookSystemBackByPanelSwitcher();
    }

    
    public boolean onBackPressed(){
        return mHelper != null && mHelper.hookSystemBackByPanelSwitcher();
    }

    private void log(String m) {
        JLog.e(TAG, m);
    }
}
