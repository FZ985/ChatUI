package io.chat.kit.chat.messagelist.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.chat.kit.R;
import io.chat.kit.model.UiMessage;
import io.chat.kit.provider.ChatProvider;
import io.im.core.MessageType;
import io.im.core.message.im.RevokeMessage;
import io.im.core.model.Message;
import io.im.core.model.MessageContent;
import io.im.uicommon.adapter.IViewProviderListener;
import io.im.uicommon.adapter.ViewHolder;
import io.im.uicommon.helper.OptionsHelper;
import io.im.uicommon.listener.FixViewClickListener;
import io.im.uicommon.utils.MessageCheck;

/**
 * author : JFZ
 * date : 2026/6/11 14:36
 * description :撤回消息
 */
public class IRevokeMessageProvider extends BaseMessageItemProvider<RevokeMessage> {

    public IRevokeMessageProvider() {
        mConfig.showTime = false;
        mConfig.showPortrait = false;
        mConfig.centerInHorizontal = true;
        mConfig.showWarning = false;
        mConfig.showContentBubble = false;
    }

    @Override
    protected ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_message_revoke, parent, false));
    }

    @Override
    protected void bindContentViewHolder(ViewHolder parentHolder, ViewHolder contentHolder, RevokeMessage msgContent, UiMessage uiMessage, boolean isSender, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        TextView msg = contentHolder.getView(R.id.revoke_tv);
        TextView edit = contentHolder.getView(R.id.revoke_edit_tv);

        OptionsHelper.updateTextSize(msg, 13);
        OptionsHelper.updateTextSize(edit, 13);

        if (isSender) {
            msg.setText(R.string.kit_message_revoke);

            Message revokeMessage = msgContent.getRevokeMessage();
            if (revokeMessage == null) {
                edit.setVisibility(View.GONE);
            } else {
                if (revokeMessage.getMessageType() == MessageType.CHAT_TEXT && MessageCheck.checkRevokeMessage(uiMessage.getMessage(), ChatProvider.getOptions().revokeTime)) {
                    edit.setVisibility(View.VISIBLE);
                } else {
                    edit.setVisibility(View.GONE);
                }
            }
            edit.setOnClickListener(new FixViewClickListener() {
                @Override
                protected void onViewClick(@NonNull View v) {
                    if (listener != null) {
                        listener.onViewClick(v, MessageClickType.REVOKE_EDIT, position, uiMessage);
                    }
                }
            });
        } else {
            edit.setVisibility(View.GONE);
            msg.setText(contentHolder.getContext().getString(R.string.kit_message_revoke_from, uiMessage.getMessage().getFromUser().getName()));
        }
    }

    @Override
    protected boolean isMessageViewType(@Nullable MessageContent messageContent) {
        return messageContent != null && messageContent instanceof RevokeMessage;
    }

    @Override
    protected boolean canEdit() {
        return false;
    }

    @Override
    public Spannable getSummarySpannable(Context context, RevokeMessage revokeMessage) {
        Spannable revokeMessageSummarySpannable = revokeMessage.getSummarySpannable(context);
        if (revokeMessageSummarySpannable != null) {
            return revokeMessageSummarySpannable;
        }
        return new SpannableString(context.getString(R.string.kit_message_revoke));
    }

    @Override
    protected boolean onItemClick(ViewHolder holder, View view, RevokeMessage revokeMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        return true;
    }
}
