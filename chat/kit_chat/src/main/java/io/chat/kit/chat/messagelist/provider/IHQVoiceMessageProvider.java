package io.chat.kit.chat.messagelist.provider;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.text.TextUtilsCompat;

import java.util.List;
import java.util.Locale;

import io.chat.kit.R;
import io.chat.kit.chat.voice.AudioRecordManager;
import io.im.uicommon.model.UiMessage;
import io.im.core.message.im.HQVoiceMessage;
import io.im.core.model.MessageContent;
import io.im.core.model.State;
import io.im.core.utils.ChatNetworkUtil;
import io.im.core.utils.JLog;
import io.im.uicommon.adapter.IViewProviderListener;
import io.im.uicommon.adapter.ViewHolder;

/**
 * author : JFZ
 * date : 2024/1/27 14:36
 * description :
 */
public class IHQVoiceMessageProvider extends BaseMessageItemProvider<HQVoiceMessage> {

    private static final String TAG = "HQVoiceMessageProvider";

    public IHQVoiceMessageProvider() {
        mConfig.showReadState = true;
        mConfig.showContentBubble = false;
    }

    @Override
    protected ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_message_hqvoice, parent, false));
    }

    @Override
    protected void bindContentViewHolder(ViewHolder parentHolder, ViewHolder contentHolder, HQVoiceMessage message, UiMessage uiMessage, boolean isSender, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        contentHolder.setBackgroundRes(R.id.voice_bg, isSender ? R.drawable.kit_bg_bubble_right : R.drawable.kit_bg_bubble_left);
        int minWidth = 70, maxWidth = 204;
        float scale = parentHolder.getContext().getResources().getDisplayMetrics().density;
        minWidth = (int) (minWidth * scale + 0.5f);
        maxWidth = (int) (maxWidth * scale + 0.5f);
        int duration = AudioRecordManager.getInstance().getMaxVoiceDuration();
        View rcVoiceBgView = contentHolder.getView(R.id.voice_bg);
        TextView rcDuration = contentHolder.getView(R.id.voice_duration);
        if (!checkViewsValid(rcVoiceBgView, rcDuration)) {
            JLog.e(TAG, "checkViewsValid error");
            return;
        }
        rcVoiceBgView.getLayoutParams().width = (int) (minWidth + (maxWidth - minWidth) / duration * message.getDuration());
        if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == LayoutDirection.RTL) {
            contentHolder.setText(R.id.voice_duration, String.format("\"%s", message.getDuration() + ""));
        } else {
            contentHolder.setText(R.id.voice_duration, String.format("%s\"", message.getDuration() + ""));
        }

        if (isSender) {
            AnimationDrawable animationDrawable = (AnimationDrawable) ContextCompat.getDrawable(contentHolder.getContext(), R.drawable.voice_play_send);
            contentHolder.setVisible(R.id.voice_receive, false);
            contentHolder.setVisible(R.id.voice_send, true);
            rcDuration.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) rcDuration.getLayoutParams();
            lp.setMarginEnd(12);
            rcDuration.setLayoutParams(lp);
            if (uiMessage.isPlaying()) {
                contentHolder.setImageDrawable(R.id.voice_send, animationDrawable);
                if (animationDrawable != null) {
                    animationDrawable.start();
                }
            } else {
                contentHolder.setImageResource(R.id.voice_send, R.drawable.voice_vec_right3);
            }
            contentHolder.setVisible(R.id.voice_unread, false);
            contentHolder.setVisible(R.id.voice_download_error, false);
            contentHolder.setVisible(R.id.voice_download_progress, false);
        } else {
            AnimationDrawable animationDrawable = (AnimationDrawable) ContextCompat.getDrawable(contentHolder.getContext(), R.drawable.voice_play_receive);
            contentHolder.setVisible(R.id.voice_receive, true);
            contentHolder.setVisible(R.id.voice_send, false);
            rcDuration.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) rcDuration.getLayoutParams();
            lp.setMarginStart(12);
            rcDuration.setLayoutParams(lp);
            if (uiMessage.isPlaying()) {
                contentHolder.setImageDrawable(R.id.voice_receive, animationDrawable);
                if (animationDrawable != null) {
                    animationDrawable.start();
                }
            } else {
                contentHolder.setImageResource(R.id.voice_receive, R.drawable.voice_vec_left3);
            }
            if (message.getLocalUri() != null) {
                contentHolder.setVisible(R.id.voice_download_error, false);
                contentHolder.setVisible(R.id.voice_download_progress, false);
                contentHolder.setVisible(R.id.voice_unread, false);
                contentHolder.setVisible(R.id.voice_unread, !message.isListened());
            } else {
                if (uiMessage.getState() == State.ERROR
                        || !ChatNetworkUtil.isConnection(contentHolder.getContext())) {
                    contentHolder.setVisible(R.id.voice_unread, false);
                    contentHolder.setVisible(R.id.voice_download_error, true);
                    contentHolder.setVisible(R.id.voice_download_progress, false);
                } else if (uiMessage.getState() == State.PROGRESS) {
                    contentHolder.setVisible(R.id.voice_unread, false);
                    contentHolder.setVisible(R.id.voice_download_error, false);
                    contentHolder.setVisible(R.id.voice_download_progress, true);
                } else {
                    contentHolder.setVisible(R.id.voice_unread, true);
                    contentHolder.setVisible(R.id.voice_download_error, false);
                    contentHolder.setVisible(R.id.voice_download_progress, false);
                }
            }
        }
    }

    @Override
    protected boolean isMessageViewType(@Nullable MessageContent messageContent) {
        return messageContent != null && messageContent instanceof HQVoiceMessage;
    }


    @Override
    protected boolean onItemClick(ViewHolder holder, View view, HQVoiceMessage voiceMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        if (listener != null) {
            listener.onViewClick(holder.itemView, io.im.core.MessageClickType.AUDIO_CLICK, position, uiMessage);
            return true;
        }
        return false;
    }

    @Override
    public Spannable getSummarySpannable(Context context, HQVoiceMessage voiceMessage) {
        Spannable summarySpannable = voiceMessage.getSummarySpannable(context);
        if (summarySpannable != null) {
            return summarySpannable;
        }
        return new SpannableString("[" + context.getString(R.string.voice_tip) + "]");
    }
}
