package io.im.kit.conversation.messagelist.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.im.kit.R;
import io.im.kit.model.UiMessage;
import io.im.kit.widget.adapter.IViewProviderListener;
import io.im.kit.widget.adapter.ViewHolder;
import io.im.lib.message.UnKnowMessage;
import io.im.lib.model.MessageContent;

/**
 * author : JFZ
 * date : 2024/1/27 11:48
 * description :
 */
public class IUnKnowMessageProvider implements ConversationMessageProvider<UnKnowMessage> {
    @Override
    public boolean isSummaryType(MessageContent messageBody) {
        return messageBody instanceof UnKnowMessage;
    }

    @Override
    public Spannable getSummarySpannable(Context context, UnKnowMessage messageContent) {
        return new SpannableString(context.getString(R.string.kit_message_unknown));
    }

    @Override
    public boolean isItemViewType(UiMessage item) {
        return true;
    }

    @Override
    public void bindViewHolder(ViewHolder holder, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kit_item_message_unknow, parent, false);
        return new ViewHolder(parent.getContext(), view);
    }

}
