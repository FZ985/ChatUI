package io.im.kit.conversation.extension;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import io.im.kit.config.enums.ChatInputMode;
import io.im.kit.conversation.IConversationFragment;
import io.im.kit.conversation.extension.component.emoticon.ChatAndroidEmoji;
import io.im.lib.utils.JLog;

/**
 * author : JFZ
 * date : 2024/1/26 16:25
 * description :
 */
public final class ChatExtensionViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> mExtensionBoardState = new MutableLiveData<>();
    private final MutableLiveData<ChatInputMode> mInputModeLiveData = new MutableLiveData<>();

    private boolean isSoftInputShow;

    @SuppressLint("StaticFieldLeak")
    private EditText editText;

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

    public void setAttachChat(IConversationFragment fragment, EditText editText) {
        this.editText = editText;
        this.editText.addTextChangedListener(mTextWatcher);
    }

    public void onSendClick() {
        JLog.e("======onSendClick");
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
                imm.showSoftInput(editText, 0);
            } else {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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
}
