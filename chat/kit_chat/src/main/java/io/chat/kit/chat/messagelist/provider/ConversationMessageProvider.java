package io.chat.kit.chat.messagelist.provider;

import io.chat.kit.model.UiMessage;
import io.im.uicommon.adapter.IViewProvider;
import io.im.core.model.MessageContent;

public interface ConversationMessageProvider<T extends MessageContent>
        extends IViewProvider<UiMessage>, ConversationSummaryProvider<T> {
}
