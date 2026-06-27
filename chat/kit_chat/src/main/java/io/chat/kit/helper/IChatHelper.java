package io.chat.kit.helper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.chat.kit.R;
import io.chat.kit.chat.IChatFragment;
import io.chat.kit.chat.extension.ChatExtensionViewModel;
import io.chat.kit.chat.extension.component.emoticon.ChatEmoticonBoard;
import io.chat.kit.chat.extension.component.plugins.ChatPluginBoard;
import io.chat.kit.chat.extension.component.plugins.ChatPluginModule;
import io.chat.kit.config.enums.ChatInputMode;
import io.chat.kit.config.enums.InputStyle;
import io.chat.kit.provider.InitChatProvider;
import io.im.core.listener.ChatLifecycle;
import io.im.core.model.Message;
import io.im.core.model.UserConvert;
import io.im.core.utils.ChatLibUtil;
import io.im.core.utils.JLog;
import io.im.uicommon.IMCenter;
import io.im.uicommon.MessageOperate;
import io.im.uicommon.config.ChatMessageProvider;
import io.im.uicommon.event.ChatMessageEvent;
import io.im.uicommon.event.PermissionEvent;
import io.im.uicommon.helper.ChatPermissionHelper;
import io.im.uicommon.listener.IPermissionProxy;
import io.im.uicommon.listener.MessageEventListener;
import io.im.uicommon.route.RouterConstant;
import io.im.uicommon.widgets.switchpanel.PanelSwitchHelper;
import io.im.uicommon.widgets.switchpanel.interfaces.ContentScrollMeasurer;
import io.im.uicommon.widgets.switchpanel.interfaces.PanelHeightMeasurer;
import io.im.uicommon.widgets.switchpanel.interfaces.listener.OnPanelChangeListener;
import io.im.uicommon.widgets.switchpanel.interfaces.listener.OnViewClickListener;
import io.im.uicommon.widgets.switchpanel.view.panel.IPanelView;
import io.im.uicommon.widgets.switchpanel.view.panel.PanelView;

/**
 * author : JFZ
 * date : 2024/1/30 17:09
 * description :
 */
public final class IChatHelper implements ChatLifecycle, OnViewClickListener, MessageEventListener {

    private static final String TAG = "IChatHelper";
    private PanelSwitchHelper mHelper;

    private IChatFragment mFragment;

    private int listUnfilledHeight = 0;

    private Context mContext;

    private ChatExtensionViewModel mExtensionViewModel;

    @Nullable
    private Message referMessage;

    private int referIndex = -1;

    public void bindConversation(Context context, IChatFragment fragment) {
        if (fragment == null || context == null) return;
        this.mContext = context;
        this.mFragment = fragment;
        this.mFragment.getBinding().inputPanel.setInputStyle(InputStyle.setType(
                fragment.requireActivity().getIntent().getIntExtra(RouterConstant.INPUT_STYLE, InputStyle.All.getType())));
        mExtensionViewModel = new ViewModelProvider(fragment).get(ChatExtensionViewModel.class);
        mExtensionViewModel.getInputModeLiveData().observe(fragment, mode -> fragment.getBinding().inputPanel.updateInputMode(mode));
        mExtensionViewModel.setAttachChat(fragment, fragment.getBinding().inputPanel.getEditText());
        fragment.getBinding().inputPanel.getBinding().send.setOnClickListener(this::onClickBefore);
        fragment.getBinding().inputPanel.getBinding().voice.setOnClickListener(this::onClickBefore);
        fragment.getBinding().recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager != null && (layoutManager instanceof LinearLayoutManager)) {
                    int position = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    View lastChildView = layoutManager.findViewByPosition(position);
                    if (lastChildView != null) {
                        int bottom = lastChildView.getBottom();
                        int listHeight = fragment.getBinding().recycler.getHeight() - fragment.getBinding().recycler.getPaddingBottom();
                        listUnfilledHeight = listHeight - bottom;
                    }
                }
            }
        });

        mFragment.getBinding().inputPanel.setSendVoiceCall(result -> MessageOperate.sendVoiceMessage(fragment.getUser(), fragment.getConversationType(), result, referMessage, null));

        IMCenter.getInstance().getOptions().addMessageEventListener(this);
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
//                        log("系统键盘是否可见 : " + visible + " 高度为：" + height);
                    })
                    .addEditTextFocusChangeListener((view, hasFocus) -> {
//                        log("输入框是否获得焦点 : " + hasFocus);
                        if (hasFocus) {
                            scrollToBottom(referIndex);
                        }
                        mFragment.getBinding().inputPanel.onEditTextFocus(hasFocus);
                    })
                    .addPanelHeightMeasurer(new PanelHeightMeasurer() {
                        @Override
                        public boolean synchronizeKeyboardHeight() {
                            return false;
                        }

                        @Override
                        public int getTargetPanelDefaultHeight() {
                            return ChatLibUtil.dip2px(mFragment.getConversationActivity(), 370);
                        }

                        @Override
                        public int getPanelTriggerId() {
                            return R.id.emoji;
                        }
                    })
                    .addContentScrollMeasurer(new ContentScrollMeasurer() {
                        @Override
                        public int getScrollDistance(int defaultDistance) {
                            return defaultDistance - listUnfilledHeight;
                        }

                        @Override
                        public int getScrollViewId() {
                            return mFragment.getBinding().recycler.getId();
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
                            if (panel == null) return;
                            if (panel instanceof PanelView) {
                                PanelView panelView = (PanelView) panel;
                                if (panelView.getId() == R.id.panel_emotion) {
                                    ChatEmoticonBoard emoticonBoard = mFragment.getBinding().panelEmotion.findViewById(R.id.emoji_board);
                                    emoticonBoard.initEmoji(mFragment);
                                } else if (panelView.getId() == R.id.panel_addition) {
                                    ChatPluginBoard pluginBoard = mFragment.getBinding().panelAddition.findViewById(R.id.plugin_board);
                                    pluginBoard.initPlugin(mFragment, referMessage);
                                }
                            }
                        }
                    })
                    .addViewClickListener(IChatHelper.this)
                    .build();
        }

//        mFragment.getBinding().recycler.setTouchCall(this::closeExpand);
    }

    private void scrollToBottom(int index) {
        if (mFragment != null) {
            mFragment.scrollToBottom(index);
        }
    }


    private void switchVoice(View view) {
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
    }

    @Override
    public void onClickBefore(@Nullable View view) {
        if (view != null && mFragment != null) {
            int id = view.getId();
            if (id == R.id.voice) {
                IPermissionProxy permissionProxy = IMCenter.getInstance().getOptions().getPermissionProxy();
                if (permissionProxy != null) {
                    permissionProxy.requestPermission(mFragment.getActivity(),
                            new PermissionEvent(PermissionEvent.AUDIO),
                            () -> switchVoice(view));
                } else {
                    ChatPermissionHelper.request(mFragment,
                            Manifest.permission.RECORD_AUDIO,
                            view.getContext().getString(R.string.permission_msg_send_audio, ChatLibUtil.getAppName(view.getContext())),
                            () -> switchVoice(view));
                }
            } else if (id == R.id.edit) {
                //点击 输入框
                mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.TextInput);
                mExtensionViewModel.setSoftInputKeyBoard(true, false);
            } else if (id == R.id.emoji) {
                //点击 表情按钮
                ChatInputMode value = mExtensionViewModel.getInputModeLiveData().getValue();
                if (value == ChatInputMode.EmoticonMode) {
                    mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.TextInput);
                    mExtensionViewModel.setSoftInputKeyBoard(true, false);
                } else {
                    if (value == ChatInputMode.NormalMode) {
                        mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.EmoticonMode);
                        mExtensionViewModel.setSoftInputKeyBoard(false, true);
                        view.postDelayed(() -> mExtensionViewModel.clearFocus(), 250);
                    } else {
                        mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.EmoticonMode);
                        mExtensionViewModel.setSoftInputKeyBoard(false, false);
                    }
                }
            } else if (id == R.id.add) {
                //点击 添加插件
                if (mExtensionViewModel.getInputModeLiveData().getValue() != null
                        && mExtensionViewModel.getInputModeLiveData().getValue() == ChatInputMode.PluginMode
                ) {
                    mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.TextInput);
                    mExtensionViewModel.setSoftInputKeyBoard(true, false);
                } else {
                    mExtensionViewModel.getInputModeLiveData().postValue(ChatInputMode.PluginMode);
                    mExtensionViewModel.setSoftInputKeyBoard(false, true);
                }
            } else if (id == R.id.send) {
                //点击 发送按钮
                mExtensionViewModel.onSendClick(referMessage);
            }
//            log("点击了View : " + view);
        }
    }

    public void setReferMessage(@Nullable Message referMessage, int index) {
        this.referMessage = referMessage;
        this.referIndex = index;
        if (mFragment != null) {
            if (referMessage != null) {
                mFragment.getBinding().referLl.setVisibility(View.VISIBLE);
                mFragment.getBinding().referClose.setOnClickListener(v -> setReferMessage(null, -1));

                UserConvert user = referMessage.getFromUser();
                Spannable spannable = ChatMessageProvider.getMessageSummary(mFragment.getActivity(), referMessage.getMessageContent());
                SpannableStringBuilder sb = new SpannableStringBuilder(user.getName() + "：");
                sb.append(spannable);
                mFragment.getBinding().referTv.setText(sb);
                io.im.uicommon.helper.OptionsHelper.updateTextSize(mFragment.getBinding().referTv, 13);

                mFragment.getBinding().inputPanel.postDelayed(() -> mFragment.getBinding().inputPanel.getEditText().performClick(), 150);
            } else {
                mFragment.getBinding().referLl.setVisibility(View.GONE);
            }
        }
    }

    public void setRevokeMessageAgain(@NonNull String content, @Nullable Message referMessage, int index) {
        setReferMessage(referMessage, index);
        EditText editText = mFragment.getBinding().inputPanel.getEditText();
        editText.setText(content);
        editText.setSelection(content.length());
        onClickBefore(editText);
    }

    @Override
    public void onPause() {
//        if (mFragment != null) {
//            mFragment.getBinding().switchLayout.recycle();
//            mHelper = null;
//        }
    }

    public boolean closeExpand() {
        mExtensionViewModel.getInputModeLiveData().setValue(ChatInputMode.NormalMode);
        mHelper.resetState();
        return true;
    }

    @Override
    public void onSendMessage(ChatMessageEvent event) {
        //发送完成后刷新引用布局
        setReferMessage(null, -1);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mFragment != null) {
            for (ChatPluginModule plugin : InitChatProvider.getOptions().getPluginConfig().getPluginModules(mFragment.getUser())) {
                plugin.onPluginActivityResult(mFragment, requestCode, resultCode, data);
            }
        }
        return ChatLifecycle.super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        for (ChatPluginModule plugin : InitChatProvider.getOptions().getPluginConfig().getPluginModules(mFragment.getUser())) {
            plugin.onPluginDestroy();
        }
    }

    public boolean onBackPressed() {
        return mHelper != null && mHelper.hookSystemBackByPanelSwitcher();
    }

    private void log(String m) {
        JLog.e(TAG, m);
    }
}
