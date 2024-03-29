package io.im.kit.conversation.messagelist.provider;

import android.content.Context;
import android.text.Spannable;

import io.im.lib.model.MessageContent;

public interface ConversationSummaryProvider<T extends MessageContent> {

    /**
     * 是否为本模板处理的消息内容。
     *
     * @param messageContent 待处理的消息内容
     * @return 是否处理。true 代表是本模板需要处理的消息，上层会继续调用模板的 {@link #getSummarySpannable(Context,
     * MessageContent)} ()} 获取资源。 false 代表不是本模板需要处理的消息。
     */
    boolean isSummaryType(MessageContent messageContent);


    /**
     * 在会话列表页某条会话最后一条消息为该类型消息时，会话里需要展示的内容。 比如: 图片消息在会话里需要展示为"图片"，那返回对应的字符串资源即可。
     */
    Spannable getSummarySpannable(Context context, T t);

}
