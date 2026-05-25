package io.im.kit.helper;


import android.text.Spannable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import java.util.List;

import io.im.kit.IMCenter;
import io.im.kit.chat.messagelist.provider.MessageClickType;
import io.im.kit.databinding.ChatItemDefaultReplyBinding;
import io.im.kit.model.UiMessage;
import io.im.kit.widget.adapter.IViewProviderListener;
import io.im.lib.model.MessageContent;

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
        Spannable spannable = IMCenter.getInstance().getOptions().getChatConfig().getMessageSummary(replyContentView.getContext(), messageContent);
        StringBuilder sb = new StringBuilder(uiMessage.getMessage().getInnerReplyMessage().getFromUser().getUserName() + "：");
        sb.append(spannable);
        binding.defReplyTv.setText(sb);
        replyContentView.setOnClickListener(v -> listener.onViewClick(v, MessageClickType.REPLY_CONTENT_CLICK, position, uiMessage));
        replyContentView.setOnLongClickListener(v -> listener.onViewLongClick(v, MessageClickType.REPLY_CONTENT_CLICK, position, uiMessage));
    }
}
