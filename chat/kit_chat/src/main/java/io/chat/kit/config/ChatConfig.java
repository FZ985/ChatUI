package io.chat.kit.config;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;

import java.util.ArrayList;
import java.util.List;

import io.chat.kit.chat.messagelist.provider.ConversationMessageProvider;
import io.chat.kit.chat.messagelist.provider.ConversationSummaryProvider;
import io.chat.kit.chat.messagelist.provider.IForwardMessageProvider;
import io.chat.kit.chat.messagelist.provider.IImageMessageProvider;
import io.chat.kit.chat.messagelist.provider.ITextMessageProvider;
import io.chat.kit.chat.messagelist.provider.IUnKnowMessageProvider;
import io.chat.kit.model.UiMessage;
import io.im.uicommon.adapter.ProviderManager;
import io.im.core.MessageType;
import io.im.core.message.im.ForwardMessage;
import io.im.core.message.im.ImageMessage;
import io.im.core.message.im.TextMessage;
import io.im.core.model.MessageContent;

/**
 * author : JFZ
 * date : 2024/1/27 11:15
 * description :
 */
public class ChatConfig {

    private final ProviderManager<UiMessage> mMessageListProvider = new ProviderManager<>();
    private final ConversationMessageProvider defaultMessageProvider = new IUnKnowMessageProvider();
    private final List<ConversationSummaryProvider> mConversationSummaryProviders = new ArrayList<>();


    public ChatConfig() {
        initMessageProvider();
    }

    private void initMessageProvider() {
        mMessageListProvider.setDefaultProvider(defaultMessageProvider);
        addMessageProvider(MessageType.CHAT_TEXT, new ITextMessageProvider(), TextMessage.class);
        addMessageProvider(MessageType.CHAT_IMAGE, new IImageMessageProvider(), ImageMessage.class);
        addMessageProvider(MessageType.CHAT_FORWARD, new IForwardMessageProvider(), ForwardMessage.class);
    }

    public ProviderManager<UiMessage> getConversationProvider() {
        return mMessageListProvider;
    }

    public void addMessageProvider(int messageType, ConversationMessageProvider provider, Class<? extends MessageContent> messageClass) {
        if (provider != null) {
            mMessageListProvider.addProvider(provider);
            mConversationSummaryProviders.add(provider);
        }
        MessageType.addChatType(messageType, messageClass);
    }

    //获取聊天页面级
    public MessageContent getMessageContent(int messageType) {
        return MessageType.getMessageContent(messageType);
    }

    //添加应用级别消息内容
    public void addAppMessageContent(int messageType, Class<? extends MessageContent> messageClass) {
        MessageType.addAppMessageType(messageType, messageClass);
    }

    //获取应用级消息内容
    public MessageContent getAppMessageContent(int messageType) {
        return MessageType.getAppMessageContent(messageType);
    }


    /**
     * 获得消息展示信息
     *
     * @param context        上下文
     * @param messageContent 消息类型
     */
    public Spannable getMessageSummary(Context context, MessageContent messageContent) {
        Spannable spannable = new SpannableString("");
        if (messageContent == null) {
            return spannable;
        }
        Spannable defaultSpannable = defaultMessageProvider.getSummarySpannable(context, messageContent);
        for (ConversationSummaryProvider item : mConversationSummaryProviders) {
            if (item.isSummaryType(messageContent)) {
                spannable = item.getSummarySpannable(context, messageContent);
            }
        }
        return spannable == null ? defaultSpannable : spannable;
    }

}
