package io.chat.kit.chat.messagelist.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.chat.kit.R;
import io.im.uicommon.helper.OptionsHelper;
import io.im.uicommon.adapter.IViewProviderListener;
import io.im.uicommon.adapter.ViewHolder;
import io.im.core.model.MessageContent;

/**
 * author : JFZ
 * date : 2024/1/27 11:48
 * description :
 */
public class IUnKnowMessageProvider implements ConversationMessageProvider {
    @Override
    public boolean isSummaryType(MessageContent messageBody) {
        return isItemViewType(messageBody);
    }

    @Override
    public boolean isItemViewType(Object item) {
        return true;
    }

    @Override
    public Spannable getSummarySpannable(Context context, MessageContent messageContent) {
        Spannable summarySpannable = messageContent.getSummarySpannable(context);
        if (summarySpannable != null) {
            return summarySpannable;
        }
        return new SpannableString(context.getString(R.string.kit_message_unknown));
    }

    @Override
    public void bindViewHolder(ViewHolder holder, Object o, int position, List list, IViewProviderListener listener) {
        TextView un_know_tv = holder.getView(R.id.un_know_tv);
        OptionsHelper.updateTextSize(un_know_tv, 13);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_message_unknow, parent, false);
        return new ViewHolder(parent.getContext(), view);
    }

}
