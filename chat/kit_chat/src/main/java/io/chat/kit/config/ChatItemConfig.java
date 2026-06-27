package io.chat.kit.config;

import io.chat.kit.chat.messagelist.provider.IForwardMessageProvider;
import io.chat.kit.chat.messagelist.provider.IHQVoiceMessageProvider;
import io.chat.kit.chat.messagelist.provider.IImageMessageProvider;
import io.chat.kit.chat.messagelist.provider.IRevokeMessageProvider;
import io.chat.kit.chat.messagelist.provider.ITextMessageProvider;
import io.im.core.MessageType;
import io.im.core.message.im.ForwardMessage;
import io.im.core.message.im.HQVoiceMessage;
import io.im.core.message.im.ImageMessage;
import io.im.core.message.im.RevokeMessage;
import io.im.core.message.im.TextMessage;
import io.im.uicommon.config.ChatMessageProvider;

/**
 * author : JFZ
 * date : 2024/1/27 11:15
 * description :
 */
public final class ChatItemConfig {

    private ChatItemConfig() {
    }

    public static void initMessageProvider() {
        ChatMessageProvider.addMessageProvider(MessageType.CHAT_TEXT, new ITextMessageProvider(), TextMessage.class);
        ChatMessageProvider.addMessageProvider(MessageType.CHAT_IMAGE, new IImageMessageProvider(), ImageMessage.class);
        ChatMessageProvider.addMessageProvider(MessageType.CHAT_FORWARD, new IForwardMessageProvider(), ForwardMessage.class);
        ChatMessageProvider.addMessageProvider(MessageType.CHAT_VOICE, new IHQVoiceMessageProvider(), HQVoiceMessage.class);
        ChatMessageProvider.addMessageProvider(MessageType.CHAT_REVOKE, new IRevokeMessageProvider(), RevokeMessage.class);
    }
}
