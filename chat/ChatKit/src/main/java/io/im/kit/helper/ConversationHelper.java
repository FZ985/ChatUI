package io.im.kit.helper;

import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import io.im.kit.R;
import io.im.kit.config.enums.ChatInputMode;
import io.im.kit.config.enums.InputStyle;
import io.im.kit.conversation.IConversationFragment;
import io.im.kit.conversation.extension.ChatExtensionViewModel;
import io.im.kit.conversation.extension.component.emoticon.ChatEmoticonBoard;
import io.im.kit.conversation.extension.component.plugins.ChatPluginBoard;
import io.im.kit.utils.RouteUtil;
import io.im.kit.widget.switchpanel.PanelSwitchHelper;
import io.im.kit.widget.switchpanel.interfaces.PanelHeightMeasurer;
import io.im.kit.widget.switchpanel.interfaces.listener.OnPanelChangeListener;
import io.im.kit.widget.switchpanel.interfaces.listener.OnViewClickListener;
import io.im.kit.widget.switchpanel.view.panel.IPanelView;
import io.im.kit.widget.switchpanel.view.panel.PanelView;
import io.im.lib.callback.ChatLifecycle;
import io.im.lib.utils.ChatLibUtil;
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
                    .addKeyboardStateListener((visible, height) -> {
                        log("系统键盘是否可见 : " + visible + " 高度为：" + height);
                    })
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
                    .addPanelHeightMeasurer(new PanelHeightMeasurer() {
                        @Override
                        public boolean synchronizeKeyboardHeight() {
                            return false;
                        }

                        @Override
                        public boolean forceUseTargetPanelDefaultHeight() {
                            return true;
                        }

                        @Override
                        public int getTargetPanelDefaultHeight() {
                            return ChatLibUtil.dip2px(mFragment.getConversationActivity(), 380);
                        }

                        @Override
                        public int getPanelTriggerId() {
                            return R.id.emoji;
                        }
                    })
                    .addPanelChangeListener(new OnPanelChangeListener() {
                        @Override
                        public void onKeyboard() {

                        }

                        @Override
                        public void onNone() {

                        }

                        @Override
                        public void onPanel(@Nullable IPanelView panel) {

                        }

                        @Override
                        public void onPanelSizeChange(@Nullable IPanelView panel, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            if (panel instanceof PanelView) {
                                PanelView panelView = (PanelView) panel;
                                if (panelView.getId() == R.id.panel_emotion) {
                                    ChatEmoticonBoard emoticonBoard = mFragment.getBinding().panelEmotion.findViewById(R.id.emoji_board);
                                    emoticonBoard.initEmoji(mFragment);
                                } else if (panelView.getId() == R.id.panel_addition) {
                                    ChatPluginBoard pluginBoard = mFragment.getBinding().panelAddition.findViewById(R.id.plugin_board);
                                    pluginBoard.initPlugin(mFragment);
                                }
                            }
                        }
                    })
                    .addViewClickListener(ConversationHelper.this)
                    .build();
        }
        mFragment.getBinding().recycler.setTouchCall(this::closeExpand);
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
                //点击 语音按钮
                if (mFragment.getBinding().inputPanel.isVoiceInputMode()) {
                    mFragment.getBinding().inputPanel.setIsVoiceInputMode(false);
                    mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.TextInput);
                    // 切换到文本输入模式后需要弹出软键盘
                    view.postDelayed(() -> {
                        mFragment.getBinding().inputPanel.getEditText().requestFocus();
                        mExtensionViewModel.setSoftInputKeyBoard(true, true);
                    }, 150);
                } else {
                    closeExpand();
                    mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.VoiceInput);
                    mFragment.getBinding().inputPanel.setIsVoiceInputMode(true);
                    mExtensionViewModel.setSoftInputKeyBoard(false, true);
                }
            } else if (id == R.id.edit) {
                //点击 输入框
                mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.TextInput);
            } else if (id == R.id.emoji) {
                //点击 表情按钮
                if (mExtensionViewModel.getInputModeLiveData().getValue() != null
                        && mExtensionViewModel.getInputModeLiveData().getValue() == ChatInputMode.EmoticonMode) {
                    mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.TextInput);
                    mExtensionViewModel.setSoftInputKeyBoard(true, false);
                } else {
                    mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.EmoticonMode);
                    mExtensionViewModel.setSoftInputKeyBoard(false, false);
                }
            } else if (id == R.id.add) {
                //点击 添加插件
                if (mExtensionViewModel.getInputModeLiveData().getValue() != null
                        && mExtensionViewModel.getInputModeLiveData().getValue() == ChatInputMode.PluginMode
                ) {
                    mExtensionViewModel.getInputModeLiveData().setValue(ChatInputMode.TextInput);
                    mExtensionViewModel.setSoftInputKeyBoard(true, false);
                } else {
                    mExtensionViewModel.getInputModeLiveData().setValue(ChatInputMode.PluginMode);
                    mExtensionViewModel.setSoftInputKeyBoard(false, true);
                }
            } else if (id == R.id.send) {
                //点击 发送按钮
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

    public boolean closeExpand() {
        mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.NormalMode);
        return mHelper.hookSystemBackByPanelSwitcher();
    }


    public boolean onBackPressed() {
        return mHelper != null && closeExpand();
    }

    private void log(String m) {
        JLog.e(TAG, m);
    }
}
