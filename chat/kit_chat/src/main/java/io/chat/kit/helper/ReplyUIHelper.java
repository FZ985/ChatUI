package io.chat.kit.helper;


import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import java.util.List;

import io.im.core.model.MessageContent;
import io.chat.kit.chat.messagelist.provider.MessageClickType;
import io.chat.kit.databinding.ChatItemDefaultReplyBinding;
import io.chat.kit.model.UiMessage;
import io.chat.kit.provider.ChatProvider;
import io.im.uicommon.adapter.IViewProviderListener;

/**
 * by DAD FZ
 * 2026/5/25
 * desc：
 **/
public class ReplyUIHelper {

    private ChatItemDefaultReplyBinding binding;

    public void bindReplyContent(
            FrameLayout replyContentView,
            UiMessage uiMessage,
            boolean isSender,
            int position,
            List<UiMessage> list,
            IViewProviderListener<UiMessage> listener) {
        binding = ChatItemDefaultReplyBinding.inflate(LayoutInflater.from(replyContentView.getContext()), replyContentView, true);
        MessageContent messageContent = uiMessage.getMessage().getInnerReplyMessage().getMessageContent();
        Spannable spannable = ChatProvider.getOptions().getChatConfig().getMessageSummary(replyContentView.getContext(), messageContent);
        SpannableStringBuilder sb = new SpannableStringBuilder(uiMessage.getMessage().getInnerReplyMessage().getFromUser().getUserName() + "：");
        sb.append(spannable);
        binding.defReplyTv.setText(sb);
        replyContentView.setOnClickListener(v -> listener.onViewClick(v, MessageClickType.REPLY_CONTENT_CLICK, position, uiMessage));
        replyContentView.setOnLongClickListener(v -> listener.onViewLongClick(v, MessageClickType.REPLY_CONTENT_CLICK, position, uiMessage));
    }
}
