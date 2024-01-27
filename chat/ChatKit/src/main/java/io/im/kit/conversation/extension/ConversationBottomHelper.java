package io.im.kit.conversation.extension;

import androidx.lifecycle.ViewModelProvider;

import io.im.kit.callback.ConversationUserCall;
import io.im.kit.callback.ConversationUserUpdate;
import io.im.kit.conversation.ConversationFragment;
import io.im.kit.widget.component.inputpanel.ChatInputPanel;
import io.im.lib.callback.ChatLifecycle;

/**
 * author : JFZ
 * date : 2024/1/26 16:08
 * description :
 */
public final class ConversationBottomHelper implements ChatLifecycle, ConversationUserUpdate {

    private ConversationFragment fragment;
    private ChatInputPanel chatInputPanel;
    private ConversationUserCall userCall;

    private ChatExtensionViewModel mExtensionViewModel;


    public void bindUI(ConversationFragment fragment, ChatInputPanel chatInputPanel, ConversationUserCall userCall) {
        this.fragment = fragment;
        this.chatInputPanel = chatInputPanel;
        this.userCall = userCall;
        this.chatInputPanel.bindUI(fragment, userCall);
        mExtensionViewModel = new ViewModelProvider(fragment).get(ChatExtensionViewModel.class);
        mExtensionViewModel.setAttachChat(fragment, this.chatInputPanel.getEditText());
    }

    @Override
    public void updateConversationUserCall(ConversationUserCall userCall) {
        this.userCall = userCall;
        if (chatInputPanel != null) {
            chatInputPanel.updateConversationUserCall(userCall);
        }
    }

    @Override
    public void onDestroy() {
        if (chatInputPanel != null) {
            chatInputPanel.onDestroy();
        }
    }
}
