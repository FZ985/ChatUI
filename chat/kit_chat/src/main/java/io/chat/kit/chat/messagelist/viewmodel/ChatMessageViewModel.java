package io.chat.kit.chat.messagelist.viewmodel;


import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.chat.kit.chat.extension.ChatExtCall;
import io.chat.kit.chat.messagelist.provider.MessageClickType;
import io.chat.kit.chat.voice.AudioPlayManager;
import io.chat.kit.event.PageEvent;
import io.chat.kit.event.ScrollToEndEvent;
import io.chat.kit.listener.IAudioPlayListener;
import io.chat.kit.listener.IMessageViewModelProcessor;
import io.chat.kit.model.UiMessage;
import io.chat.kit.provider.ChatProvider;
import io.chat.kit.ui.popmenu.ChatPopMenu;
import io.chat.kit.ui.popmenu.IChatPopMenuClickListener;
import io.im.core.core.ChatSDK;
import io.im.core.listener.ChatLifecycle;
import io.im.core.message.im.HQVoiceMessage;
import io.im.core.model.Message;
import io.im.core.model.MessageContent;
import io.im.core.model.State;
import io.im.core.utils.ChatExecutorHelper;
import io.im.core.utils.JLog;
import io.im.uicommon.IMCenter;
import io.im.uicommon.event.ChatMessageEvent;
import io.im.uicommon.event.DeleteMessageEvent;
import io.im.uicommon.helper.ChatMsgCache;
import io.im.uicommon.listener.MessageEventListener;
import io.im.uicommon.ui.web.IWebActivity;
import io.im.uicommon.utils.SavePathUtils;
import io.im.uicommon.widgets.text.selection.SelectableTextHelper;

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

    private boolean isDestroy;

    public ChatMessageViewModel(@NonNull Application application) {
        super(application);
    }

    public void bindConversation(@NonNull ChatExtCall extCall) {
        this.mCall = extCall;
        isDestroy = false;
        IMCenter.getInstance().getOptions().addMessageEventListener(this);
    }

    public ChatExtCall getChatExtCall() {
        return mCall;
    }

    @Override
    public void onSendMessage(ChatMessageEvent event) {
        Message msg = event.getMessage();
        if (Objects.equals(mCall.getConversationType().getValue(), msg.getConversationType().getValue()) && msg.getMessageId() > 0) {
            if (!Objects.equals(mCall.getUser().getId(), msg.getToUser().getId())) {
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
            if (!Objects.equals(mCall.getUser().getId(), msg.getToUser().getId())) {
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
        if (Objects.equals(mCall.getUser().getId(), msg.getFromUser().getId())
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
        isDestroy = true;
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
            IMessageViewModelProcessor viewModelProcessor = ChatProvider.getOptions().getViewModelProcessor();
            boolean isProcess = false;
            if (viewModelProcessor != null) {
                isProcess = viewModelProcessor.onViewClick(this, clickType, data);
            }
            if (!isProcess) {
                if (clickType == MessageClickType.AUDIO_CLICK) {
                    onAudioClick(data);
                } else {
                    Toast.makeText(view.getContext(), "click", Toast.LENGTH_SHORT).show();
                }
            }
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
        IMessageViewModelProcessor viewModelProcessor = ChatProvider.getOptions().getViewModelProcessor();
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

    public boolean onClickLink(Context context, String link) {
        IMessageViewModelProcessor viewModelProcessor = ChatProvider.getOptions().getViewModelProcessor();
        boolean isProcess = false;
        if (viewModelProcessor != null) {
            isProcess = viewModelProcessor.onClickLink(context, link);
        }
        if (!isProcess) {
            boolean result = false;
            String str = link.toLowerCase();
            if (str.startsWith("http") || str.startsWith("https")) {
                IWebActivity.startWeb(context, link);
                result = true;
            }
            return result;
        } else {
            return true;
        }
    }

    public boolean onTextSelected(View view, int position, UiMessage uiMessage, String text, boolean isSelectAll, int eventAction) {
        IChatPopMenuClickListener listener = ChatProvider.getOptions().popMenuClickListener;
        if (listener == null || listener.onTextSelected(view, position, uiMessage.getMessage(), text, isSelectAll, eventAction)) {

//            if (messageInfo.isRevoked()) {
//                return false;
//            }

            if (eventAction == MotionEvent.ACTION_MOVE) {
                if (popMenu != null && popMenu.isShowing()) {
                    popMenu.hide();
                }
            } else if (eventAction == MotionEvent.ACTION_UP || eventAction == MotionEvent.ACTION_CANCEL) {
                // show pop menu
                if (popMenu == null) {
                    popMenu = new ChatPopMenu();
                }
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

    public void onAudioClick(UiMessage uiMessage) {
        // 处理暂停逻辑
        MessageContent content = uiMessage.getMessage().getMessageContent();
        if (content instanceof HQVoiceMessage) {
            if (AudioPlayManager.getInstance().isPlaying()) {
                Uri playingUri = AudioPlayManager.getInstance().getPlayingUri();
                AudioPlayManager.getInstance().stopPlay();
                // 暂停的是当前播放的 Uri
                if (playingUri.equals(((HQVoiceMessage) content).getLocalUri())) return;
            }
            // 如果被 voip 占用通道，则不播放，弹提示框
//            if (AudioPlayManager.getInstance().isInVOIPMode(getApplication())) {
//                mPageEventLiveData.setValue(new ToastEvent(getApplication().getString(R.string.rc_voip_occupying)));
//                return;
//            }
            playOrDownloadHQVoiceMsg((HQVoiceMessage) uiMessage.getMessage().getMessageContent(), uiMessage);
        }
    }

    private void playOrDownloadHQVoiceMsg(HQVoiceMessage content, UiMessage uiMessage) {
        boolean ifDownloadHQVoiceMsg = !content.isLocalExit(getApplication());
        if (ifDownloadHQVoiceMsg) {
            downloadHQVoiceMsg(uiMessage);
        } else {
            playVoiceMessage(uiMessage);
        }
    }

    private void downloadHQVoiceMsg(final UiMessage uiMessage) {
        MessageContent body = uiMessage.getMessage().getMessageContent();
        if (body instanceof HQVoiceMessage) {
            HQVoiceMessage hqVoiceMessage = (HQVoiceMessage) body;
            File file = new File(((HQVoiceMessage) body).getLocalPath());
            File savePath = SavePathUtils.getSavePath(getApplication().getCacheDir());
            String path = savePath.getAbsolutePath();
            String fileName = file.getName();
//            DownLoadInfo info = new DownLoadInfo(hqVoiceMessage.getUrl(), path, fileName);
//            DownloadModel.get()
//                    .download(info, (progress, percent, length) -> ChatExecutorHelper.getInstance().mainThread().execute(() -> {
//                        if (!isDestroy) {
//                            uiMessage.setState(State.PROGRESS);
//                            uiMessage.setProgress(percent.intValue());
//                            refreshSingleMessage(uiMessage);
//                        }
//                    }), completeFile -> ChatExecutorHelper.getInstance().mainThread().execute(() -> {
//                        if (!isDestroy) {
//                            uiMessage.setState(State.NORMAL);
//                            refreshSingleMessage(uiMessage);
//                            playVoiceMessage(uiMessage);
//                        }
//                    }), () -> ChatExecutorHelper.getInstance().mainThread().execute(() -> {
//                        if (!isDestroy) {
//                            uiMessage.setState(State.ERROR);
//                            refreshSingleMessage(uiMessage);
//                        }
//                    }));
        }
    }

    private void playVoiceMessage(final UiMessage uiMessage) {
        final MessageContent content = uiMessage.getMessage().getMessageContent();
        Uri voicePath = null;
        if (content instanceof HQVoiceMessage) {
            voicePath = ((HQVoiceMessage) content).getLocalUri();
        }
        Log.e("ddd", "voicePath:" + voicePath);
        if (voicePath != null) {
            AudioPlayManager.getInstance().startPlay(
                    getApplication(),
                    voicePath,
                    new IAudioPlayListener() {
                        @Override
                        public void onStart(Uri uri) {
                            uiMessage.setPlaying(true);
                            refreshSingleMessage(uiMessage);
                        }

                        @Override
                        public void onStop(Uri uri) {
                            uiMessage.setPlaying(false);
                            refreshSingleMessage(uiMessage);
                        }

                        @Override
                        public void onComplete(Uri uri) {
                            uiMessage.setPlaying(false);
                            // 找到下个播放消息继续播放
                            if (uiMessage.getMessage()
                                    .getMessageDirection()
                                    .equals(Message.MessageDirection.RECEIVE)) {
                                refreshSingleMessage(uiMessage);
                            } else {
                                refreshSingleMessage(uiMessage);
                                // 不切换线程会造成，ui 一直显示播放的 bug
                                ChatExecutorHelper.getInstance()
                                        .mainThread()
                                        .execute(() -> findNextHQVoice(uiMessage));
                            }
                        }
                    });
        }
    }

    private void findNextHQVoice(UiMessage uiMessage) {
        if (!IMCenter.getInstance().getOptions().isRc_play_audio_continuous()) {
            JLog.e("TAG", "rc_play_audio_continuous is disabled.");
            return;
        }
        int position = findUIMessageIndexById(uiMessage.getMessage().getMessageId());
        if (position == -1) {
            JLog.e("the message isn't found in the list.");
            return;
        }
        for (int i = position; i < mUiMessages.size(); i++) {
            UiMessage item = mUiMessages.get(i);
            if (item.getMessage().getMessageContent() instanceof HQVoiceMessage) {
                if (!TextUtils.equals(item.getMessage().getFromUser().getId(), ChatSDK.getConnectUser().getId())) {
                    onAudioClick(item);
                    break;
                }
            }
        }
    }
}
