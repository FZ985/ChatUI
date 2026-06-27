package io.im.uicommon.config;


import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;

import java.util.ArrayList;
import java.util.List;

import io.im.core.MessageType;
import io.im.core.model.MessageContent;
import io.im.uicommon.adapter.ProviderManager;
import io.im.uicommon.model.UiMessage;
import io.im.uicommon.providers.ConversationMessageProvider;
import io.im.uicommon.providers.ConversationSummaryProvider;
import io.im.uicommon.providers.IUnKnowMessageProvider;

/**
 * by DAD FZ
 * 2026/6/27
 * desc：
 **/
public final class ChatMessageProvider {

    private static final ProviderManager<UiMessage> mMessageListProvider = new ProviderManager<>();

    private static final ConversationMessageProvider defaultMessageProvider = new IUnKnowMessageProvider();

    private static final List<ConversationSummaryProvider> mConversationSummaryProviders = new ArrayList<>();

    private ChatMessageProvider() {
    }

    static {
        mMessageListProvider.setDefaultProvider(defaultMessageProvider);
    }


    public static ProviderManager<UiMessage> getConversationProvider() {
        return mMessageListProvider;
    }

    public static void addMessageProvider(int messageType, ConversationMessageProvider provider, Class<? extends MessageContent> messageClass) {
        if (provider != null) {
            mMessageListProvider.addProvider(provider);
            mConversationSummaryProviders.add(provider);
        }
        MessageType.addChatType(messageType, messageClass);
    }

    //获取聊天页面级
    public static MessageContent getMessageContent(int messageType) {
        return MessageType.getMessageContent(messageType);
    }

    //添加应用级别消息内容
    public static void addAppMessageContent(int messageType, Class<? extends MessageContent> messageClass) {
        MessageType.addAppMessageType(messageType, messageClass);
    }

    //获取应用级消息内容
    public static MessageContent getAppMessageContent(int messageType) {
        return MessageType.getAppMessageContent(messageType);
    }


    /**
     * 获得消息展示信息
     *
     * @param context        上下文
     * @param messageContent 消息类型
     */
    public static Spannable getMessageSummary(Context context, MessageContent messageContent) {
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
