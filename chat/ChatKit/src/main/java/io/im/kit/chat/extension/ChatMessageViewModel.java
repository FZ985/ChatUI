package io.im.kit.chat.extension;


import android.app.Application;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.im.kit.IMCenter;
import io.im.kit.chat.messagelist.provider.MessageClickType;
import io.im.kit.event.PageEvent;
import io.im.kit.event.actionevent.ChatMessageEvent;
import io.im.kit.event.actionevent.DeleteMessageEvent;
import io.im.kit.event.uievent.ScrollToEndEvent;
import io.im.kit.listener.IMessageViewModelProcessor;
import io.im.kit.listener.MessageEventListener;
import io.im.kit.model.UiMessage;
import io.im.kit.ui.popmenu.ChatPopMenu;
import io.im.kit.ui.popmenu.IChatPopMenuClickListener;
import io.im.kit.widget.text.selection.SelectableTextHelper;
import io.im.lib.callback.ChatLifecycle;
import io.im.lib.helper.ChatMsgCache;
import io.im.lib.model.Message;
import io.im.lib.model.State;
import io.im.lib.utils.JLog;

/**
 * by DAD FZ
 * 2026/5/21
 * desc：
 **/
public final class ChatMessageViewModel extends AndroidViewModel implements ChatLifecycle, MessageEventListener {

    private final MediatorLiveData<Boolean> mIsEditStatus = new MediatorLiveData<>(false);
    private final MediatorLiveData<PageEvent> mPageEventLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<List<UiMessage>> mUiMessageLiveData = new MediatorLiveData<>();

    private final List<UiMessage> mUiMessages = new ArrayList<>();

    private ChatExtCall mCall;

    private ChatPopMenu popMenu;

    public ChatMessageViewModel(@NonNull Application application) {
        super(application);
    }

    public void bindConversation(@NonNull ChatExtCall extCall) {
        this.mCall = extCall;
        IMCenter.getInstance().getOptions().addMessageEventListener(this);
    }

    public ChatExtCall getChatExtCall() {
        return mCall;
    }

    @Override
    public void onSendMessage(ChatMessageEvent event) {
        Message msg = event.getMessage();
        if (Objects.equals(mCall.getConversationType().getValue(), msg.getConversationType().getValue()) && msg.getMessageId() > 0) {
            if (!Objects.equals(mCall.getUser().getUserId(), msg.getToUser().getUserId())) {
                return;
            }
            UiMessage uiMessage = findUIMessageById(msg.getMessageId());
            boolean isAdd = uiMessage == null;
            if (isAdd) {
                uiMessage = mapUIMessage(msg);
            } else {
                uiMessage.setMessage(msg);
            }
            switch (event.getEvent()) {
                case ChatMessageEvent.ATTACH:
                    uiMessage.setState(State.PROGRESS);
                    break;
                case ChatMessageEvent.ERROR:
                    uiMessage.setState(State.ERROR);
                    break;
                case ChatMessageEvent.SUCCESS:
                    uiMessage.setState(State.SUCCESS);
                    break;
            }
            if (isAdd) {
                sendMessageEvent(uiMessage);
            } else {
                refreshSingleMessage(uiMessage);
            }
        }
    }

    @Override
    public void onSendMediaMessage(ChatMessageEvent event) {
        Message msg = event.getMessage();
        if (Objects.equals(mCall.getConversationType().getValue(), msg.getConversationType().getValue()) && msg.getMessageId() > 0) {
            if (!Objects.equals(mCall.getUser().getUserId(), msg.getToUser().getUserId())) {
                return;
            }
            UiMessage uiMessage = findUIMessageById(msg.getMessageId());
            boolean isAdd = uiMessage == null;
            if (isAdd) {
                uiMessage = mapUIMessage(msg);
            } else {
                uiMessage.setMessage(msg);
            }
            switch (event.getEvent()) {
                case ChatMessageEvent.ATTACH:
                    break;
                case ChatMessageEvent.PROGRESS:
                    uiMessage.setState(State.PROGRESS);
                    uiMessage.setProgress(event.getProgress());
                    break;
                case ChatMessageEvent.ERROR:
                    uiMessage.setState(State.ERROR);
                    break;
                case ChatMessageEvent.SUCCESS:
                    uiMessage.setProgress(100);
                    uiMessage.setState(State.NORMAL);
                    break;
                case ChatMessageEvent.CANCEL:
                    uiMessage.setState(State.CANCEL);
                    break;
            }
            uiMessage.setMessage(msg);
            if (isAdd) {
                sendMessageEvent(uiMessage);
            } else {
                refreshSingleMessage(uiMessage);
            }
        }
    }

    @Override
    public void onReceiveMessage(ChatMessageEvent event) {
        Message msg = event.getMessage();
        if (Objects.equals(mCall.getUser().getUserId(), msg.getFromUser().getUserId())
                && msg.getMessageId() > 0) {
            UiMessage uiMessage = findUIMessageById(msg.getMessageId());
            boolean isAdd = uiMessage == null;
            if (isAdd) {
                uiMessage = mapUIMessage(msg);
            } else {
                uiMessage.setMessage(msg);
            }
            uiMessage.setState(State.NORMAL);
            if (isAdd) {
                sendMessageEvent(uiMessage);
            } else {
                refreshSingleMessage(uiMessage);
            }
        }
        if (isEdit()) {
            setEdit(false);
        }
    }

    @Override
    public void onDeleteMessage(DeleteMessageEvent event) {
        if (event.getEvent() == DeleteMessageEvent.SUCCESS) {
            List<Message> messages = event.getMessages();
            removeMessage(messages);
            if (isEdit()) {
                setEdit(false);
            }
        }
    }

    public void setEdit(boolean edit) {
        mIsEditStatus.setValue(edit);
        if (!edit) {
            ChatMsgCache.clear();
            mCall.checkMultiSelectView();
        }
        for (UiMessage m : mUiMessages) {
            m.setEdit(edit);
            if (!edit) {
                m.setSelected(false);
            }
        }
        refreshAllMessage(false);
    }

    public boolean isEdit() {
        return (mIsEditStatus.getValue() != null && mIsEditStatus.getValue());
    }

    public UiMessage mapUIMessage(Message message) {
        UiMessage uiMessage = new UiMessage(message);
        if (mIsEditStatus.getValue() != null) {
            uiMessage.setEdit(mIsEditStatus.getValue());
        }
        return uiMessage;
    }

    public UiMessage findUIMessageById(long messageId) {
        UiMessage uiMessage = null;
        for (UiMessage item : mUiMessages) {
            if (item.getMessage().getMessageId() == messageId) {
                uiMessage = item;
                break;
            }
        }
        return uiMessage;
    }

    public int findUIMessageIndexById(long messageId) {
        int position = -1;
        for (int i = 0; i < mUiMessages.size(); i++) {
            UiMessage item = mUiMessages.get(i);
            if (item.getMessage().getMessageId() == messageId) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void removeMessage(List<Message> list) {
        List<UiMessage> uiMessageList = new ArrayList<>();
        for (Message m : list) {
            UiMessage uiMessage = findUIMessageById(m.getMessageId());
            if (uiMessage != null) {
                uiMessageList.add(uiMessage);
            }
        }
        if (!uiMessageList.isEmpty()) {
            mUiMessages.removeAll(uiMessageList);
            refreshAllMessage();
        }
    }

    public void refreshAllMessage() {
        refreshAllMessage(true);
    }

    public void refreshAllMessage(boolean force) {
        if (force) {
            for (UiMessage item : mUiMessages) {
                item.change();
            }
        }
        excListLiveData();
    }

    private void excListLiveData() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            mUiMessageLiveData.setValue(mUiMessages);
        } else {
            mUiMessageLiveData.postValue(mUiMessages);
        }
    }

    public void refreshSingleMessage(UiMessage uiMessage) {
        int position = findUIMessageIndexById(uiMessage.getMessage().getMessageId());
        if (position != -1) {
            uiMessage.setChange(true);
            mUiMessageLiveData.postValue(mUiMessages);
        }
    }

    public void addLocalMessages(Message... messages) {
        ArrayList<UiMessage> list = new ArrayList<>();
        for (Message message : messages) {
            boolean contains = false;
            for (UiMessage uiMessage : mUiMessages) {
                if (uiMessage.getMessage().getMessageId() == message.getMessageId()) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                list.add(mapUIMessage(message));
            }
        }

        if (!list.isEmpty()) {
            mUiMessages.addAll(list);
            mUiMessageLiveData.setValue(mUiMessages);
            executePageEvent(new ScrollToEndEvent());
        }
    }

    public void clearAllMessages() {
        mUiMessages.clear();
        refreshAllMessage();
        executePageEvent(new ScrollToEndEvent());
    }

    public void executePageEvent(PageEvent pageEvent) {
        if (Looper.getMainLooper().getThread().equals(Thread.currentThread())) {
            mPageEventLiveData.setValue(pageEvent);
        } else {
            mPageEventLiveData.postValue(pageEvent);
        }
    }

    private void sendMessageEvent(UiMessage uiMessage) {
        mUiMessages.add(uiMessage);
        refreshAllMessage();
        executePageEvent(new ScrollToEndEvent());
    }

    @Override
    public void onResume() {
        ChatLifecycle.super.onResume();
    }

    @Override
    public void onPause() {
        ChatLifecycle.super.onPause();
    }

    @Override
    public void onDestroy() {
        ChatMsgCache.clear();
        IMCenter.getInstance().getOptions().removeMessageEventListener(this);
    }

    public MediatorLiveData<PageEvent> getPageEventLiveData() {
        return mPageEventLiveData;
    }

    public MediatorLiveData<List<UiMessage>> getUiMessageLiveData() {
        return mUiMessageLiveData;
    }

    public void onViewClick(View view, int clickType, int position, UiMessage data) {
        if (clickType == MessageClickType.EDIT_CLICK) {
            checkSelectMessage(data);
        } else if (clickType == MessageClickType.REPLY_CONTENT_CLICK) {
            Toast.makeText(view.getContext(), "点击回复", Toast.LENGTH_SHORT).show();
        } else {
            if (popMenu != null && !popMenu.isShowing()) {
                SelectableTextHelper.getInstance().dismiss();
            }
            Toast.makeText(view.getContext(), "click", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkSelectMessage(@NonNull UiMessage data) {
        //编辑中
        boolean isSelect = !data.isSelected();
        if (isSelect) {
            ChatMsgCache.addMessage(data.getMessage());
        } else {
            ChatMsgCache.removeMessage(data.getMessageId());
        }
        data.setSelected(isSelect);
        refreshSingleMessage(data);
        mCall.checkMultiSelectView();
    }

    public boolean onViewLongClick(View view, int clickType, int position, UiMessage data) {
        IMessageViewModelProcessor viewModelProcessor =
                IMCenter.getInstance().getOptions().getViewModelProcessor();
        boolean isProcess = false;
        if (viewModelProcessor != null) {
            isProcess = viewModelProcessor.onViewLongClick(this, clickType, data);
        }
        if (!isProcess) {
            if (clickType == MessageClickType.CONTENT_LONG_CLICK) {
                return onItemMessageLongClick(view, clickType, position, data);
            } else if (clickType == MessageClickType.REPLY_CONTENT_CLICK) {
                Toast.makeText(view.getContext(), "长按点击回复", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean onTextSelected(View view, int position, UiMessage uiMessage, String text, boolean isSelectAll) {
        IChatPopMenuClickListener listener = IMCenter.getInstance().getOptions().popMenuClickListener;
        if (listener == null || listener.onTextSelected(view, position, uiMessage.getMessage(), text, isSelectAll)) {

//            if (messageInfo.isRevoked()) {
//                return false;
//            }

            // show pop menu
            if (popMenu == null) {
                popMenu = new ChatPopMenu();
            }
            JLog.e("onTextSelected:" + uiMessage.getMessage());

            if (isSelectAll) {
                popMenu.show(view.getContext(), view, uiMessage.getMessage(), uiMessage.isSender());
            } else {
                popMenu.show(
                        view.getContext(),
                        view,
                        text,
                        uiMessage.getMessage(),
                        uiMessage.isSender());
            }
        }
        return true;
    }

    private boolean onItemMessageLongClick(View view, int clickType, int position, UiMessage data) {
        if (popMenu != null && popMenu.isShowing()) {
            popMenu.hide();
        }
        popMenu = new ChatPopMenu();
        //popMenu.setDismissListener(() -> SelectableTextHelper.getInstance().dismiss());
        popMenu.show(view.getContext(), view, data.getMessage(), data.isSender());
        return true;
    }


    public boolean onBackPressed() {
        if (mIsEditStatus.getValue() != null && mIsEditStatus.getValue()) {
            setEdit(false);
            return true;
        }
        return false;
    }
}
