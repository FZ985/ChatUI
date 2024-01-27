package io.im.kit.conversation.extension;

import android.annotation.SuppressLint;
import android.app.Application;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import io.im.kit.config.ChatInputMode;
import io.im.kit.conversation.ConversationFragment;

/**
 * author : JFZ
 * date : 2024/1/26 16:25
 * description :
 */
public final class ChatExtensionViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> mExtensionBoardState = new MutableLiveData<>();
    private final MutableLiveData<ChatInputMode> mInputModeLiveData = new MutableLiveData<>();

    @SuppressLint("StaticFieldLeak")
    private EditText editText;

    public ChatExtensionViewModel(@NonNull Application application) {
        super(application);
    }

    void setAttachChat(ConversationFragment fragment, EditText editText) {
        this.editText = editText;
    }

    public void onSendClick() {

    }

    public EditText getEditText() {
        return editText;
    }

    public MutableLiveData<Boolean> getExtensionBoardState() {
        return mExtensionBoardState;
    }

    public MutableLiveData<ChatInputMode> getInputModeLiveData() {
        return mInputModeLiveData;
    }
}
