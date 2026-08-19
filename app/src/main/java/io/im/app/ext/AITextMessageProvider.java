package io.im.app.ext;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

import io.chat.kit.chat.messagelist.provider.BaseMessageItemProvider;
import io.im.app.databinding.AppChatItemMessageAiBinding;
import io.im.core.model.MessageContent;
import io.im.uicommon.adapter.IViewProviderListener;
import io.im.uicommon.adapter.ViewHolder;
import io.im.uicommon.helper.OptionsHelper;
import io.im.uicommon.model.UiMessage;

/**
 * author : JFZ
 * date : 2024/1/27 14:36
 * description :
 */
public class AITextMessageProvider extends BaseMessageItemProvider<io.im.core.message.im.AIMessage> {

    private AppChatItemMessageAiBinding binding;

    @Override
    protected ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        binding = AppChatItemMessageAiBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ViewHolder(parent.getContext(), binding.getRoot());
    }

    @Override
    protected void bindContentViewHolder(ViewHolder parentHolder, ViewHolder contentHolder, io.im.core.message.im.AIMessage msgContent, UiMessage uiMessage, boolean isSender, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        binding.msgTextAi.stopTyping();

//        if (msgContent.isGenerating()) {
//            // ==========AI正在输出中==========
//            binding.msgTextAi.startTypeWriter(msgContent.getContent(), binding.msgTextAi.getPlayPosition());
//        } else {
//            // ==========已经完成的历史消息，直接渲染完整文本，禁止打字动画==========
//            binding.msgTextAi.setFullTextNoAnimate(msgContent.getContent());
//        }

        if (msgContent.isGenerating()) {
            // ==========AI正在输出中==========
            binding.msgTextAi.startMarkdownTypeWriter(msgContent.getContent(), binding.msgTextAi.getPlayPosition());
        } else {
            // ==========已经完成的历史消息，直接渲染完整文本，禁止打字动画==========
            binding.msgTextAi.setNoAnimate(msgContent.getContent());
        }

//        if(msg.isGenerating){
//            tvLoading.visibility = View.VISIBLE
//            btnCopy.visibility = View.GONE
//        }else{
//            tvLoading.visibility = View.GONE
//            btnCopy.visibility = View.VISIBLE
//        }

        binding.msgTextAi.setTextColor(isSender ?
                ContextCompat.getColor(contentHolder.getContext(), io.im.core.R.color.chat_white_90)
                : ContextCompat.getColor(contentHolder.getContext(), io.im.core.R.color.chat_skin_Text));
        OptionsHelper.updateTextSize(binding.msgTextAi, 15);

    }

    @Override
    protected boolean isMessageViewType(@Nullable MessageContent messageContent) {
        return messageContent != null && messageContent instanceof io.im.core.message.im.AIMessage;
    }

    @Override
    public Spannable getSummarySpannable(Context context, io.im.core.message.im.AIMessage textMessage) {
        Spannable textMessageSummarySpannable = textMessage.getSummarySpannable(context);
        if (textMessageSummarySpannable != null) {
            return textMessageSummarySpannable;
        }
        return new SpannableString("[AI]");
    }

    @Override
    protected boolean onItemClick(ViewHolder holder, View view, io.im.core.message.im.AIMessage aiMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        return true;
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        binding.msgTextAi.stopTyping();
    }
}
