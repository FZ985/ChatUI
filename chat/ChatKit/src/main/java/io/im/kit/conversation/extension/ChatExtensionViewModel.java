package io.im.kit.conversation.extension;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import io.im.kit.config.ChatInputMode;
import io.im.kit.conversation.IConversationFragment;
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

    public ChatExtensionViewModel(@NonNull Application application) {
        super(application);
    }

    void setAttachChat(IConversationFragment fragment, EditText editText) {
        this.editText = editText;
    }

    public void onSendClick() {
        JLog.e("======onSendClick");
    }

    // 收起面板，ChatExtension 仅显示 InputPanel。
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
