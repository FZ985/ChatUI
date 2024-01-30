package io.im.kit.conversation.extension;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import io.im.kit.callback.ConversationUserCall;
import io.im.kit.callback.ConversationUserUpdate;
import io.im.kit.config.ChatInputMode;
import io.im.kit.conversation.ConversationFragment;
import io.im.kit.utils.KeyboardHeightUtil;
import io.im.kit.utils.keyboard.KeyboardHeightObserver;
import io.im.kit.utils.keyboard.KeyboardHeightProvider;
import io.im.kit.widget.component.inputpanel.ChatInputPanel;
import io.im.kit.widget.component.plugins.PluginPanel;
import io.im.lib.callback.ChatLifecycle;
import io.im.lib.utils.JLog;

/**
 * author : JFZ
 * date : 2024/1/26 16:08
 * description :
 */
public final class ConversationBottomHelper implements ChatLifecycle, ConversationUserUpdate {

    private ConversationFragment fragment;
    private ChatInputPanel chatInputPanel;
    private PluginPanel pluginPanel;
    private ConversationUserCall userCall;

    private ChatExtensionViewModel mExtensionViewModel;

    private KeyboardHeightProvider keyboardHeightProvider = null;

    private boolean editTextIsFocused = false;
    private ChatInputMode mPreInputMode;

    private final KeyboardHeightObserver mKeyboardHeightObserver = new KeyboardHeightObserver() {
        @Override
        public void onKeyboardHeightChanged(int orientation, boolean isOpen, int keyboardHeight) {
            if (getActivityFromFragment() != null) {
                log("=====onKeyboardHeightChanged:" + isOpen);
                if (isOpen) {
                    int saveKeyBoardHeight = KeyboardHeightUtil.getSaveKeyBoardHeight(getContext(), orientation);
                    if (saveKeyBoardHeight != keyboardHeight) {
                        KeyboardHeightUtil.saveKeyboardHeight(getContext(), orientation, keyboardHeight);
                        updateBoardContainerHeight();
                    }
                    pluginPanel.setVisibility(View.VISIBLE);
                    pluginPanel.goneAll();
//                    YLViewUtils.addView(board_container, mPluginBoard.getView());
                    mExtensionViewModel.getExtensionBoardState().setValue(true);
                } else {
                    if (mExtensionViewModel != null) {
                        if (mExtensionViewModel.isSoftInputShow()) {
                            mExtensionViewModel.setSoftInputKeyBoard(false, false);
                        }
                        if (mPreInputMode != null
                                && (mPreInputMode == ChatInputMode.TextInput
                                || mPreInputMode == ChatInputMode.VoiceInput)) {
                            pluginPanel.setVisibility(View.GONE);
                            mExtensionViewModel.getExtensionBoardState().setValue(false);
                        }
                    }
                }
            }
        }
    };


    public void bindUI(ConversationFragment fragment, ChatInputPanel chatInputPanel, PluginPanel pluginPanel, ConversationUserCall userCall) {
        this.fragment = fragment;
        this.chatInputPanel = chatInputPanel;
        this.pluginPanel = pluginPanel;
        this.userCall = userCall;
        this.chatInputPanel.bindUI(fragment, userCall);
        mExtensionViewModel = new ViewModelProvider(fragment).get(ChatExtensionViewModel.class);
        mExtensionViewModel.setAttachChat(fragment, this.chatInputPanel.getEditText());
        mExtensionViewModel.getInputModeLiveData().observe(fragment, chatInputMode -> {
            mPreInputMode = chatInputMode;
            updateInputMode(chatInputMode);
        });
    }

    private void updateInputMode(ChatInputMode mode) {
//        if (mode == null) {
//            return;
//        }
//        if (mode == ChatInputMode.TextInput) {
//            EditText editText = mExtensionViewModel.getEditText();
//            if (editText == null || editText.getText() == null) {
//                return;
//            }
//
////            if (isEditTextSameProperty(editText)) {
////                return;
////            }
//
//            updateBoardContainerHeight();
//
//            if (!useKeyboardHeightProvider()) {
//                mExtensionViewModel.getExtensionBoardState().setValue(false);
//            } else {
//                mExtensionViewModel.getExtensionBoardState().setValue(true);
//            }
//
//            if ((editText.isFocused() || editText.getText().length() > 0)) {
//                editText.postDelayed(() -> {
//                    if (fragment != null
//                            && fragment.getActivity() != null
//                            && !fragment.getActivity().isFinishing()) {
//                        mExtensionViewModel.setSoftInputKeyBoard(true);
//                    }
//                }, 100);
//            } else {
//                mExtensionViewModel.setSoftInputKeyBoard(false);
//                mExtensionViewModel.getExtensionBoardState().setValue(false);
//            }
//        }
    }

    @Override
    public void onResume() {
//        if (mExtensionViewModel == null || chatInputPanel == null || pluginPanel == null) {
//            return;
//        }
//        if (useKeyboardHeightProvider()) {
//            keyboardHeightProvider = new KeyboardHeightProvider(getActivityFromFragment());
//            keyboardHeightProvider.setKeyboardHeightObserver(mKeyboardHeightObserver);
//        }
//
//        pluginPanel.post(() -> {
//            KeyboardHeightProvider keyboardHeightProvider = ConversationBottomHelper.this.keyboardHeightProvider;
//            if (keyboardHeightProvider != null) {
//                keyboardHeightProvider.start();
//            }
//        });
    }

    @Override
    public void onPause() {
//        if (keyboardHeightProvider != null) {
//            keyboardHeightProvider.stop();
//            keyboardHeightProvider.setKeyboardHeightObserver(null);
//            keyboardHeightProvider = null;
//        }
//        if (mExtensionViewModel != null) {
//            if (mExtensionViewModel.getEditText() != null) {
//                editTextIsFocused = mExtensionViewModel.getEditText().isFocused();
//            }
//            if (mPreInputMode != null
//                    && mPreInputMode == ChatInputMode.TextInput
//                    && pluginPanel != null) {
//                mExtensionViewModel.collapseExtensionBoard();
//            }
//        }
    }

    private void updateBoardContainerHeight() {
        int saveKeyboardHeight = KeyboardHeightUtil.getSaveKeyBoardHeight(getContext(), getContext().getResources().getConfiguration().orientation);
        ViewGroup.LayoutParams layoutParams = pluginPanel.getLayoutParams();
        int dp282 = getContext().getResources().getDimensionPixelSize(io.im.lib.R.dimen.chat_dp282);
        if (saveKeyboardHeight <= 0
                && layoutParams.height != dp282) {
            layoutParams.height = dp282;
            pluginPanel.setLayoutParams(layoutParams);
        } else if (layoutParams.height != saveKeyboardHeight) {
            layoutParams.height = saveKeyboardHeight;
            pluginPanel.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void updateConversationUserCall(ConversationUserCall userCall) {
        this.userCall = userCall;
        if (chatInputPanel != null) {
            chatInputPanel.updateConversationUserCall(userCall);
        }
    }

    public boolean useKeyboardHeightProvider() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Activity activity = getActivityFromFragment();
            return activity != null && !activity.isInMultiWindowMode();
        }
        return false;
    }

    private Activity getActivityFromFragment() {
        Context context = fragment.getContext();
        if (context != null) {
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        if (chatInputPanel != null) {
            chatInputPanel.onDestroy();
        }
    }

    private Context getContext() {
        return fragment.getContext();
    }

    private void log(String m) {
        JLog.e("ConversationBottomHelper", m);
    }
}
