package io.im.kit.conversation.messagelist.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import io.im.kit.R;
import io.im.kit.model.UiMessage;
import io.im.kit.widget.adapter.IViewProviderListener;
import io.im.kit.widget.adapter.ViewHolder;
import io.im.lib.message.TextMessage;
import io.im.lib.model.MessageContent;

/**
 * author : JFZ
 * date : 2024/1/27 14:36
 * description :
 */
public class ITextMessageProvider extends BaseMessageItemProvider<TextMessage> {
    @Override
    protected ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext()).inflate(R.layout.kit_item_message_text, parent, false));
    }

    @Override
    protected void bindContentViewHolder(ViewHolder parentHolder, ViewHolder contentHolder, TextMessage msgContent, UiMessage uiMessage, boolean isSender, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        TextView msg_text = contentHolder.getView(R.id.msg_text);

        msg_text.setText(msgContent.getContent());
    }

    @Override
    protected boolean isMessageViewType(@Nullable MessageContent messageContent) {
        return messageContent != null && messageContent instanceof TextMessage;
    }

    @Override
    public Spannable getSummarySpannable(Context context, TextMessage textMessage) {
        return new SpannableString("");
    }

    @Override
    protected boolean onItemClick(ViewHolder holder, View view, TextMessage textMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        return false;
    }
}
