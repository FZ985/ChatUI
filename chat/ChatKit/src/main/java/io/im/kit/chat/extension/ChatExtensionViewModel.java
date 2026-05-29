package io.im.kit.chat.extension;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import io.im.kit.MessageOperate;
import io.im.kit.R;
import io.im.kit.chat.IChatFragment;
import io.im.kit.chat.extension.component.emoticon.ChatAndroidEmoji;
import io.im.kit.config.enums.ChatInputMode;
import io.im.kit.widget.text.selection.SelectableTextHelper;
import io.im.lib.MessageType;
import io.im.lib.message.im.TextMessage;
import io.im.lib.model.Message;
import io.im.lib.utils.ChatToast;
import io.im.lib.utils.JLog;

/**
 * author : JFZ
 * date : 2024/1/26 16:25
 * description :
 */
public final class ChatExtensionViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> mExtensionBoardState = new MutableLiveData<>();
    private final MutableLiveData<ChatInputMode> mInputModeLiveData = new MutableLiveData<>(ChatInputMode.NormalMode);

    private boolean isSoftInputShow;

    private static final int MAX_MESSAGE_LENGTH_TO_SEND = 5500;

    @SuppressLint("StaticFieldLeak")
    private EditText editText;

    private IChatFragment fragment;
    private final TextWatcher mTextWatcher = new TextWatcher() {
        private int start;
        private int count;
        private boolean isProcess;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isProcess) {
                return;
            }
            this.start = start;
            this.count = count;
        }

        @Override
        public void afterTextChanged(Editable s) {
            //隐藏文本选择器选择框
            SelectableTextHelper.getInstance().dismiss();
            if (isProcess) {
                return;
            }
            int selectionStart = editText.getSelectionStart();
            if (ChatAndroidEmoji.isEmoji(s.subSequence(start, start + count).toString())) {
                isProcess = true;
                String resultStr = ChatAndroidEmoji.replaceEmojiWithText(s.toString());
                editText.setText(ChatAndroidEmoji.ensure(resultStr), TextView.BufferType.SPANNABLE);
                editText.setSelection(Math.min(editText.getText().length(), Math.max(0, selectionStart)));
                isProcess = false;
            }
        }
    };

    public ChatExtensionViewModel(@NonNull Application application) {
        super(application);
    }

    public void setAttachChat(IChatFragment fragment, EditText editText) {
        this.fragment = fragment;
        this.editText = editText;
        this.editText.addTextChangedListener(mTextWatcher);
    }

    public void collapseExtensionBoard() {
        if (mExtensionBoardState.getValue() != null
                && mExtensionBoardState.getValue().equals(false)) {
            JLog.e("TAG", "already collapsed, return directly.");
            return;
        }
        JLog.e("TAG", "collapseExtensionBoard");
        setSoftInputKeyBoard(false);
        mExtensionBoardState.postValue(false);
        mInputModeLiveData.postValue(ChatInputMode.NormalMode);
    }

    public void setSoftInputKeyBoard(boolean isShow) {
        forceSetSoftInputKeyBoard(isShow);
    }

    public void setSoftInputKeyBoard(boolean isShow, boolean clearFocus) {
        forceSetSoftInputKeyBoard(isShow, clearFocus);
    }

    public void forceSetSoftInputKeyBoard(boolean isShow) {
        forceSetSoftInputKeyBoard(isShow, true);
    }

    public void forceSetSoftInputKeyBoard(boolean isShow, boolean clearFocus) {
        if (editText == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getApplication().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (isShow) {
                editText.requestFocus();
                editText.setShowSoftInputOnFocus(true);
                imm.showSoftInput(editText, 0);
            } else {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                editText.setShowSoftInputOnFocus(false);
                if (clearFocus) {
                    editText.clearFocus();
                } else {
                    editText.requestFocus();
                }
            }
            isSoftInputShow = isShow;
        }
        if (isShow && mExtensionBoardState.getValue() != null && mExtensionBoardState.getValue().equals(false)) {
            mExtensionBoardState.setValue(true);
        }
    }

    public void clearFocus() {
        if (editText == null) return;
        editText.requestFocus();
        editText.setShowSoftInputOnFocus(false);
    }

    public EditText getEditText() {
        return editText;
    }

    public boolean isSoftInputShow() {
        return isSoftInputShow;
    }

    public MutableLiveData<Boolean> getExtensionBoardState() {
        return mExtensionBoardState;
    }

    public MutableLiveData<ChatInputMode> getInputModeLiveData() {
        return mInputModeLiveData;
    }

    public void onSendClick(@Nullable Message replyMessage) {
        if (editText == null) return;
        if (TextUtils.isEmpty(editText.getText())
                || TextUtils.isEmpty(editText.getText().toString().trim())) {
            JLog.e("can't send empty content.");
            editText.setText("");
            return;
        }

        String text = editText.getText().toString();
        if (text.length() > MAX_MESSAGE_LENGTH_TO_SEND) {
            ChatToast.toast(getApplication().getApplicationContext(),
                    getApplication().getString(R.string.kit_message_too_long));
            JLog.e("文本太长了。。。");
            return;
        }
        editText.setText("");
        TextMessage textMessage = TextMessage.obtain(text);
        Message message = Message.obtain(fragment.getUser(), fragment.getConversationType(), MessageType.CHAT_TEXT, textMessage);
        message.setSendStatus(Message.SentStatus.SENDING.getValue());
        MessageOperate.sendMessage(message, replyMessage, null);

    }
}
