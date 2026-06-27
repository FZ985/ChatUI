package io.im.uicommon.providers;

import io.im.core.model.MessageContent;
import io.im.uicommon.model.UiMessage;

public interface ConversationMessageProvider<T extends MessageContent>
        extends IViewProvider<UiMessage>, io.im.uicommon.providers.ConversationSummaryProvider<T> {
}
