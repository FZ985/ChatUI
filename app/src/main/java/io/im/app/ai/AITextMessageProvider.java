package io.im.app.ai;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.fluid.afm.markdown.widget.PrinterMarkDownTextView;
import com.fluid.afm.styles.MarkdownStyles;

import java.util.List;

import io.chat.kit.chat.messagelist.provider.BaseMessageItemProvider;
import io.im.app.databinding.AppChatItemMessageAiBinding;
import io.im.core.message.im.AIMessage;
import io.im.core.model.MessageContent;
import io.im.core.utils.JLog;
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

    private AIMessage msgContent;
    private UiMessage uiMessage;

    @Override
    protected ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        AppChatItemMessageAiBinding binding = AppChatItemMessageAiBinding.inflate(LayoutInflater.from(parent.getContext()));
        MarkdownStyles styles = MarkdownStyles.getDefaultStyles();
        binding.msgTextAi.init(styles, null);
        return new ViewHolder(parent.getContext(), binding.getRoot());
    }

    @Override
    protected void bindContentViewHolder(ViewHolder parentHolder, ViewHolder contentHolder, io.im.core.message.im.AIMessage msgContent, UiMessage uiMessage, boolean isSender, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        this.msgContent = msgContent;
        this.uiMessage = uiMessage;
        // ✅ binding从holder拿，不要用provider全局binding
        AppChatItemMessageAiBinding binding = AppChatItemMessageAiBinding.bind(contentHolder.itemView);
        PrinterMarkDownTextView msgTextAi = binding.msgTextAi;

        msgTextAi.setPrintingEventListener(new PrinterMarkDownTextView.PrintingEventListener() {
            @Override
            public void onPrintStart() {
                Log.e("DOG", "==onPrintStart");
            }

            @Override
            public void onPrinting() {
            }

            @Override
            public void onPrintStop(boolean printAll) {
                JLog.e("DOG", "onPrintStop");
                if (chatProcessor != null) {
                    msgContent.setComplete(true);
                    uiMessage.getMessage().updateMessageBody(msgContent);
                    //AI消息的话，实时更新
                    chatProcessor.updateMessage(uiMessage.getMessage(), () -> {
                        JLog.e("DOG", "=======update---success===");
                    });
                }
            }

            @Override
            public void onPrintPaused(int index) {
                JLog.e("==onPrintPaused:" + index);
            }

            @Override
            public void onPrintResumed() {
                JLog.e("==onPrintResumed");
            }
        });

        JLog.e("DOG", "msg===:" + msgContent.isComplete() + "," + uiMessage.getMessage().getMessageId());

        if (msgContent.isComplete()) {
            msgTextAi.restore(new PrinterMarkDownTextView.MarkDownPrintData());
            msgTextAi.setMarkdownText(msgContent.getContent());
        } else {
            msgTextAi.setPrintData(new PrinterMarkDownTextView.MarkDownPrintData());
            msgTextAi.startPrinting(msgContent.getContent());
        }

        msgTextAi.setTextColor(isSender ?
                ContextCompat.getColor(contentHolder.getContext(), io.im.core.R.color.chat_white_90)
                : ContextCompat.getColor(contentHolder.getContext(), io.im.core.R.color.chat_skin_Text));
        OptionsHelper.updateTextSize(msgTextAi, 15);

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
    public void onPageDestroy() {
        JLog.e("DOG", "onPageDestroy:" + uiMessage + "," + msgContent);
        if (msgContent != null && uiMessage != null) {
            msgContent.setComplete(true);
            uiMessage.getMessage().updateMessageBody(msgContent);
            //AI消息的话，实时更新
            chatProcessor.updateMessage(uiMessage.getMessage(), () -> {
                JLog.e("DOG", "detach=======update---success===");
            });
        }
    }
}
