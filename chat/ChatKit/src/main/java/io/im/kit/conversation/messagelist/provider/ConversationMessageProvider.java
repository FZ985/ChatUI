package io.im.kit.conversation.messagelist.provider;

import io.im.kit.model.UiMessage;
import io.im.kit.widget.adapter.IViewProvider;
import io.im.lib.model.MessageContent;

public interface ConversationMessageProvider<T extends MessageContent>
        extends IViewProvider<UiMessage>, ConversationSummaryProvider<T> {
}
