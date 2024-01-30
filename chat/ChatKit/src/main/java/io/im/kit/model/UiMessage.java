package io.im.kit.model;

import android.text.SpannableStringBuilder;

import androidx.annotation.Keep;

import io.im.lib.model.Message;
import io.im.lib.model.State;


/**
 * author : JFZ
 * date : 2023/12/11 13:53
 * description :
 */
@Keep
public class UiMessage extends UiBaseBean {

    private Message message;

    private boolean isEdit = false;//是否为编辑
    private boolean isSelected = false;//是否选中

    private @State.Value int state;

    /**
     * TextMessage  content 字段
     */
    private SpannableStringBuilder contentSpannable;

    public UiMessage(Message message) {
        setMessage(message);
        change();
    }

    public SpannableStringBuilder getContentSpannable() {
        return contentSpannable;
    }

    public void setContentSpannable(SpannableStringBuilder contentSpannable) {
        this.contentSpannable = contentSpannable;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        change();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
        if (message.getSendStatus() != null) {
            switch (message.getSendStatus()) {
                case SENDING:
                    state = State.PROGRESS;
                    break;
                case FAILED:
                    state = State.ERROR;
                    break;
                case SEND_SUCCESS:
                    state = State.SUCCESS;
                    break;
            }
        }
        change();
    }

    public long getMessageId() {
        return message != null ? message.getMessageId() : -1;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        change();
    }
}
