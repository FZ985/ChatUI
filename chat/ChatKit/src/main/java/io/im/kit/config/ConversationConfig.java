package io.im.kit.config;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.im.kit.conversation.messagelist.provider.ConversationMessageProvider;
import io.im.kit.conversation.messagelist.provider.ConversationSummaryProvider;
import io.im.kit.conversation.messagelist.provider.ITextMessageProvider;
import io.im.kit.conversation.messagelist.provider.IUnKnowMessageProvider;
import io.im.kit.model.UiMessage;
import io.im.kit.widget.adapter.ProviderManager;
import io.im.lib.message.TextMessage;
import io.im.lib.message.UnKnowMessage;
import io.im.lib.model.MessageContent;
import io.im.lib.utils.JLog;

/**
 * author : JFZ
 * date : 2024/1/27 11:15
 * description :
 */
public class ConversationConfig {

    private final ProviderManager<UiMessage> mMessageListProvider = new ProviderManager<>();
    private final ConversationMessageProvider defaultMessageProvider = new IUnKnowMessageProvider();
    private final List<ConversationSummaryProvider> mConversationSummaryProviders = new ArrayList<>();

    private final HashMap<Integer, MessageContent> msgMaps = new HashMap<>();

    public ConversationConfig() {
        initMessageProvider();
    }

    private void initMessageProvider() {
        mMessageListProvider.setDefaultProvider(defaultMessageProvider);
        addMessageProvider(MessageType.TEXT, new ITextMessageProvider(), TextMessage.class);
    }

    public ProviderManager<UiMessage> getConversationProvider() {
        return mMessageListProvider;
    }

    public void addMessageProvider(int messageType, ConversationMessageProvider provider, Class<? extends MessageContent> messageClass) {
        if (provider != null) {
            mMessageListProvider.addProvider(provider);
            mConversationSummaryProviders.add(provider);
        }
        try {
            if (messageClass != null) {
                msgMaps.put(messageType, messageClass.newInstance());
            }
        } catch (Exception e) {
            JLog.e("====存入消息体失败:" + e.getMessage());
        }
    }

    public MessageContent getMessageContent(int messageType) {
        if (msgMaps.containsKey(messageType)) {
            MessageContent messageContent = msgMaps.get(messageType);
            if (messageContent != null) {
                return messageContent;
            }
        }
        return UnKnowMessage.obtain();
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
