package io.im.kit.chat.messagelist.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import io.im.kit.IMCenter;
import io.im.kit.R;
import io.im.kit.helper.OptionsHelper;
import io.im.kit.model.UiMessage;
import io.im.kit.widget.adapter.IViewProviderListener;
import io.im.kit.widget.adapter.ViewHolder;
import io.im.lib.message.im.ForwardMessage;
import io.im.lib.model.Message;
import io.im.lib.model.MessageContent;

/**
 * author : JFZ
 * date : 2024/1/27 14:36
 * description :
 */
public class IForwardMessageProvider extends BaseMessageItemProvider<ForwardMessage> {


    public IForwardMessageProvider() {
        mConfig.showProgress = false;
        mConfig.showReadState = true;
        mConfig.showContentBubble = false;

    }

    @Override
    protected ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_message_forward, parent, false));
    }

    @Override
    protected void bindContentViewHolder(ViewHolder parentHolder, ViewHolder contentHolder, ForwardMessage message, UiMessage uiMessage, boolean isSender, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        TextView name = contentHolder.getView(R.id.name);
        TextView content1 = contentHolder.getView(R.id.content1);
        TextView content2 = contentHolder.getView(R.id.content2);
        TextView content3 = contentHolder.getView(R.id.content3);
        TextView content4 = contentHolder.getView(R.id.content4);
        TextView fLabel = contentHolder.getView(R.id.f_label);

        OptionsHelper.updateTextSize(name, 17);
        OptionsHelper.updateTextSize(fLabel, 11);

        name.setText(contentHolder.getContext().getString(R.string.forward_t3, message.getFromName(), message.getToName()));

        content1.setVisibility(View.GONE);
        content2.setVisibility(View.GONE);
        content3.setVisibility(View.GONE);
        content4.setVisibility(View.GONE);

        List<Message> messages = message.getMessages();
        if (messages.size() > 3) {
            showContent(content1, messages.get(0));
            showContent(content2, messages.get(1));
            showContent(content3, messages.get(2));
            showContent(content4, messages.get(3));
        } else if (messages.size() > 2) {
            showContent(content1, messages.get(0));
            showContent(content2, messages.get(1));
            showContent(content3, messages.get(2));
        } else if (messages.size() > 1) {
            showContent(content1, messages.get(0));
            showContent(content2, messages.get(1));
        } else if (!messages.isEmpty()) {
            showContent(content1, messages.get(0));
        }

    }

    private void showContent(TextView text, Message message) {
        text.setVisibility(View.VISIBLE);
        OptionsHelper.updateTextSize(text, 12);
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(message.getFromUser().getUserName()).append("：");
        sb.append(IMCenter.getInstance().getOptions().getChatConfig().getMessageSummary(text.getContext(), message.getMessageContent()));
        text.setText(sb);
    }

    @Override
    protected boolean isMessageViewType(@Nullable MessageContent messageContent) {
        return messageContent != null && messageContent instanceof ForwardMessage;
    }


    @Override
    protected boolean onItemClick(ViewHolder holder, View view, ForwardMessage forwardMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        return false;
    }

    @Override
    public Spannable getSummarySpannable(Context context, ForwardMessage imageMessage) {
        return new SpannableString("[" + context.getString(R.string.forward_t2) + "]");
    }
}
