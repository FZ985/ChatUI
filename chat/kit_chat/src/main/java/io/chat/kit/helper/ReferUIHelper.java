package io.chat.kit.helper;


import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import java.util.List;

import io.chat.kit.databinding.ChatItemDefaultReferBinding;
import io.im.core.MessageClickType;
import io.im.core.model.Message;
import io.im.core.model.MessageContent;
import io.im.uicommon.adapter.IViewProviderListener;
import io.im.uicommon.config.ChatMessageProvider;
import io.im.uicommon.model.UiMessage;

/**
 * by DAD FZ
 * 2026/5/25
 * desc：
 **/
public class ReferUIHelper {

    private ChatItemDefaultReferBinding binding;

    public void bindReferContent(
            FrameLayout referContentView,
            UiMessage uiMessage,
            boolean isSender,
            int position,
            List<UiMessage> list,
            IViewProviderListener<UiMessage> listener) {
        Message innerReferMessage = uiMessage.getMessage().getInnerReferMessage();
        if (innerReferMessage != null) {
            binding = ChatItemDefaultReferBinding.inflate(LayoutInflater.from(referContentView.getContext()), referContentView, true);
            MessageContent messageContent = innerReferMessage.getMessageContent();
            Spannable spannable = ChatMessageProvider.getMessageSummary(referContentView.getContext(), messageContent);
            SpannableStringBuilder sb = new SpannableStringBuilder(uiMessage.getMessage().getInnerReferMessage().getFromUser().getName() + "：");
            sb.append(spannable);
            binding.defReferTv.setText(sb);
            referContentView.setOnClickListener(v -> listener.onViewClick(v, MessageClickType.REFER_CONTENT_CLICK, position, uiMessage));
            referContentView.setOnLongClickListener(v -> listener.onViewLongClick(v, MessageClickType.REFER_CONTENT_LONG_CLICK, position, uiMessage));
        }
    }
}
