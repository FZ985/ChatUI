package io.im.kit.chat.messagelist.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.LayoutDirection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.text.TextUtilsCompat;

import java.util.List;
import java.util.Locale;

import io.im.kit.R;
import io.im.kit.chat.extension.component.emoticon.ChatAndroidEmoji;
import io.im.kit.helper.OptionsHelper;
import io.im.kit.model.UiMessage;
import io.im.kit.ui.web.IWebActivity;
import io.im.kit.utils.TextViewUtils;
import io.im.kit.widget.adapter.IViewProviderListener;
import io.im.kit.widget.adapter.ViewHolder;
import io.im.kit.widget.text.LinkTextViewMovementMethod;
import io.im.kit.widget.text.selection.SelectableTextHelper;
import io.im.lib.message.im.TextMessage;
import io.im.lib.model.MessageContent;

/**
 * author : JFZ
 * date : 2024/1/27 14:36
 * description :
 */
public class ITextMessageProvider extends BaseMessageItemProvider<TextMessage> {
    @Override
    protected ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_message_text, parent, false));
    }

    @Override
    protected void bindContentViewHolder(ViewHolder parentHolder, ViewHolder contentHolder, TextMessage msgContent, UiMessage uiMessage, boolean isSender, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        TextView textView = contentHolder.getView(R.id.msg_text);

        if (!checkViewsValid(textView)) {
            return;
        }

        if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == LayoutDirection.RTL) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }

        textView.setTag(uiMessage.getMessageId());
        if (uiMessage.getContentSpannable() == null) {
            SpannableStringBuilder spannable =
                    TextViewUtils.getSpannable(
                            msgContent.getContent(),
                            spannable1 -> {
                                uiMessage.setContentSpannable(spannable1);
                                textView.post(() -> {
                                    if (TextUtils.equals(
                                            textView.getTag() == null
                                                    ? ""
                                                    : textView.getTag().toString(),
                                            String.valueOf(
                                                    uiMessage.getMessageId())))
                                        textView.setText(
                                                uiMessage.getContentSpannable());
                                });
                            });
            uiMessage.setContentSpannable(spannable);
        }
        textView.setText(uiMessage.getContentSpannable());
        textView.setMovementMethod(new LinkTextViewMovementMethod(link -> {
            boolean result = false;
            String str = link.toLowerCase();
            if (str.startsWith("http") || str.startsWith("https")) {
                IWebActivity.startWeb(contentHolder.getContext(), link);
                result = true;
            }
            return result;
        }));

        //设置选中文本监听回调
        SelectableTextHelper.getInstance().setSelectableOnChangeListener(null);

        textView.setOnClickListener(view -> {
            SelectableTextHelper.getInstance().dismiss();
            ViewParent parent = contentHolder.itemView.getParent();
            if (parent instanceof View) {
                ((View) parent).performClick();
            }
        });

        textView.setOnLongClickListener(view -> {
            //设置选中文本监听回调
            SelectableTextHelper.getInstance()
                    .setSelectableOnChangeListener(
                            (v, pos, msg, text, isSelectAll) -> {
                                if (listener != null) {
                                    listener.onTextSelected(v, pos, uiMessage, text.toString(), isSelectAll);
                                }
                            });

            SelectableTextHelper.getInstance()
                    .showSelectView(
                            textView,
                            textView.getLayout(),
                            position,
                            uiMessage.getMessage());

            ViewParent parent = contentHolder.itemView.getParent();
            if (parent instanceof View) {
                return ((View) parent).performLongClick();
            }
            return false;
        });

        textView.setTextColor(isSender ?
                ContextCompat.getColor(contentHolder.getContext(), io.im.lib.R.color.chat_white_90)
                : ContextCompat.getColor(contentHolder.getContext(), io.im.lib.R.color.chat_skin_Text));

        OptionsHelper.updateTextSize(textView, 15);

    }

    @Override
    protected boolean isMessageViewType(@Nullable MessageContent messageContent) {
        return messageContent != null && messageContent instanceof TextMessage;
    }

    @Override
    public Spannable getSummarySpannable(Context context, TextMessage textMessage) {
        if (textMessage != null && !TextUtils.isEmpty(textMessage.getContent())) {
            String content = textMessage.getContent();
            content = content.replace("\n", " ");
            if (content.length() > 100) {
                content = content.substring(0, 100);
            }
            return new SpannableString(ChatAndroidEmoji.ensure(content));
        } else {
            return new SpannableString("");
        }
    }

    @Override
    protected boolean onItemClick(ViewHolder holder, View view, TextMessage textMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        return true;
    }
}
