package io.chat.kit.chat.messagelist.provider;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import io.chat.kit.R;
import io.chat.kit.helper.ReplyUIHelper;
import io.chat.kit.model.UiMessage;
import io.im.core.model.ConversationType;
import io.im.core.model.Message;
import io.im.core.model.MessageContent;
import io.im.core.model.State;
import io.im.core.utils.JLog;
import io.im.uicommon.IMCenter;
import io.im.uicommon.adapter.IViewProviderListener;
import io.im.uicommon.adapter.ViewHolder;
import io.im.uicommon.helper.OptionsHelper;
import io.im.uicommon.utils.DateUtil;
import io.im.uicommon.utils.KtExtKt;
import io.im.uicommon.widgets.IAvatarView;

/**
 * author : JFZ
 * date : 2024/1/27 13:42
 * description :消息总类
 */
public abstract class BaseMessageItemProvider<T extends MessageContent> implements ConversationMessageProvider<T> {

    protected MessageItemProviderConfig mConfig = new MessageItemProviderConfig();

    private final ReplyUIHelper replyUIHelper = new ReplyUIHelper();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_message_base, parent, false);
        FrameLayout contentView = rootView.findViewById(R.id.base_content);
        ViewHolder contentViewHolder = onCreateContentViewHolder(contentView, viewType);
        if (contentViewHolder != null) {
            if (contentView.getChildCount() == 0) {
                contentView.addView(contentViewHolder.itemView);
            }
        }
        return new MessageViewHolder(rootView.getContext(), rootView, contentViewHolder, mConfig);
    }

    @Override
    public void bindViewHolder(ViewHolder holder, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).setUiMessage(uiMessage);
        }
        if (uiMessage != null && uiMessage.getMessage() != null && listener != null) {
            Message message = uiMessage.getMessage();
            boolean isSender = uiMessage.getMessage().getMessageDirection().equals(Message.MessageDirection.SEND);
            holder.setVisible(R.id.base_edit_iv, uiMessage.isEdit() && canEdit());
            holder.setVisible(R.id.base_edit, uiMessage.isEdit() && canEdit());
            if (uiMessage.isEdit() && canEdit()) {
                holder.setSelected(R.id.base_edit_iv, uiMessage.isSelected());
                holder.setOnClickListener(
                        R.id.base_edit,
                        v -> listener.onViewClick(v, MessageClickType.EDIT_CLICK, position, uiMessage));
            }
            initTime(holder, position, list, message);
            initContent(holder, isSender, uiMessage, position, listener, list);
            initReply(holder, isSender, uiMessage, position, listener, list);
            initStatus(holder, uiMessage, position, listener, message, isSender, list);
            initUserInfo(holder, uiMessage, position, listener, isSender);


            if (holder instanceof MessageViewHolder) {
                T msgContent = null;
                try {
                    msgContent = (T) message.getMessageContent();
                } catch (ClassCastException e) {
                    log("bindViewHolder Message cast Exception, e:" + e.getMessage());
                }
                if (msgContent != null) {
                    bindContentViewHolder(
                            holder,
                            ((MessageViewHolder) holder).getMessageContentViewHolder(),
                            msgContent,
                            uiMessage,
                            isSender,
                            position,
                            list,
                            listener);
                } else {
                    log("bindViewHolder MessageContent cast Exception");
                }
            } else {
                log("holder is not MessageViewHolder");
            }
            uiMessage.setChange(false);
        } else {
            log("uiMessage is null");
        }
    }

    //初始化时间
    private void initTime(ViewHolder holder, int position, List<UiMessage> data, Message message) {
        TextView base_time = holder.getView(R.id.base_time);
        if (mConfig.showTime) {
            String time = DateUtil.getConversationFormatDate(message.getMessageTime(), holder.getContext());
            base_time.setText(time);
            OptionsHelper.updateTextSize(base_time, 13);
            try {
                UiMessage pre = data.get(position - 1);
                if (pre.getMessage() != null
                        && DateUtil.isShowChatTime(
                        holder.getContext(),
                        message.getMessageTime(),
                        pre.getMessage().getMessageTime(),
                        180)) {
                    base_time.setVisibility(View.VISIBLE);
                } else {
                    base_time.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                base_time.setVisibility(View.GONE);
            }
        } else {
            base_time.setVisibility(View.GONE);
        }
    }

    //初始化内容
    private void initContent(final ViewHolder holder, boolean isSender, final UiMessage uiMessage, final int position,
                             final IViewProviderListener<UiMessage> listener, final List<UiMessage> list) {
        if (mConfig.showContentBubble) {
            holder.setBackgroundRes(
                    R.id.base_content,
                    isSender ? R.drawable.kit_bg_bubble_right
                            : R.drawable.kit_bg_bubble_left);
        } else {
            holder.getView(R.id.base_content).setBackground(null);
        }
        holder.setPadding(R.id.base_content, 0, 0, 0, 0);
        LinearLayout layout = holder.getView(R.id.base_layout);
        LinearLayout bsRoot = holder.getView(R.id.bs_root);
        if (mConfig.centerInHorizontal) {
            layout.setGravity(Gravity.CENTER_HORIZONTAL);
            bsRoot.setGravity(Gravity.CENTER_HORIZONTAL);
        } else {
            layout.setGravity(isSender ? Gravity.END : Gravity.START);
            bsRoot.setGravity(isSender ? Gravity.END : Gravity.START);
        }
        holder.setOnClickListener(
                R.id.base_content,
                v -> {
                    T msgContent = null;
                    try {
                        msgContent = (T) uiMessage.getMessage().getMessageContent();
                    } catch (ClassCastException e) {
                        log("rc_content onClick MessageContent cast Exception, e:" + e.getMessage());
                    }
                    boolean result = false;
                    if (msgContent != null) {
                        result = onItemClick(
                                ((MessageViewHolder) holder).getMessageContentViewHolder(),
                                v,
                                msgContent,
                                uiMessage,
                                position,
                                list,
                                listener);
                    }
                    if (!result) {
                        listener.onViewClick(v, MessageClickType.CONTENT_CLICK, position, uiMessage);
                    }
                });

        holder.setOnLongClickListener(
                R.id.base_content,
                v -> {
                    boolean result = false;
                    T msgContent = null;
                    try {
                        msgContent = (T) uiMessage.getMessage().getMessageContent();
                    } catch (ClassCastException e) {
                        log("rc_content onLongClick MessageContent cast Exception, e:" + e.getMessage());
                    }
                    if (msgContent != null) {
                        result = onItemLongClick(((MessageViewHolder) holder).getMessageContentViewHolder(),
                                v,
                                msgContent,
                                uiMessage,
                                position,
                                list,
                                listener);
                    }
                    if (!result) {
                        return listener.onViewLongClick(v, MessageClickType.CONTENT_LONG_CLICK, position, uiMessage);
                    }
                    return true;
                });
    }

    //初始化回复内容
    private void initReply(final ViewHolder holder, boolean isSender, final UiMessage uiMessage, final int position,
                           final IViewProviderListener<UiMessage> listener, final List<UiMessage> list) {
        FrameLayout replyContentView = holder.getView(R.id.base_reply_content);
        if (uiMessage.getMessage() != null && uiMessage.getMessage().getInnerReplyMessage() != null) {
            T msgContent = null;
            try {
                msgContent = (T) uiMessage.getMessage().getMessageContent();
            } catch (ClassCastException e) {
                log("bindViewHolder Message cast Exception, e:" + e.getMessage());
            }
            if (msgContent != null) {
                replyContentView.setVisibility(View.VISIBLE);
                bindReplyContent(replyContentView, uiMessage, position, isSender, list, listener);
            } else {
                replyContentView.setVisibility(View.GONE);
            }
        } else {
            replyContentView.setVisibility(View.GONE);
        }
    }

    protected void bindReplyContent(
            FrameLayout replyContentView,
            UiMessage uiMessage,
            int position,
            boolean isSender,
            List<UiMessage> list,
            IViewProviderListener<UiMessage> listener) {
        replyUIHelper.bindReplyContent(replyContentView, uiMessage, isSender, position, list, listener);
    }

    //初始化状态
    private void initStatus(
            ViewHolder holder,
            final UiMessage uiMessage,
            final int position,
            final IViewProviderListener<UiMessage> listener,
            Message message,
            boolean isSender,
            List<UiMessage> list) {
        if (mConfig.showWarning) {
            if (isSender && uiMessage.getState() == State.ERROR) {
                holder.setVisible(R.id.base_warning, true);
                holder.setOnClickListener(
                        R.id.base_warning,
                        v -> listener.onViewClick(v, MessageClickType.WARNING_CLICK, position, uiMessage));
            } else {
                holder.setVisible(R.id.base_warning, false);
            }
        } else {
            holder.setVisible(R.id.base_warning, false);
        }

        if (mConfig.showProgress) {
            if (isSender && uiMessage.getState() == State.PROGRESS) {
                holder.setVisible(R.id.base_progress, true);
            } else {
                holder.setVisible(R.id.base_progress, false);
            }
        } else {
            holder.setVisible(R.id.base_progress, false);
        }
        initReadStatus(holder, uiMessage, position, listener, message, isSender, list);
    }

    private void initReadStatus(
            ViewHolder holder,
            final UiMessage uiMessage,
            int position,
            final IViewProviderListener<UiMessage> listener,
            final Message message,
            boolean isSender,
            List<UiMessage> list) {
        // 单聊已读状态
        if (mConfig.showReadState && isSender) {
            holder.setVisible(R.id.base_read_receipt, message.getReadStatusEnum() != Message.ReadStatus.UN_READ);
        } else {
            holder.setVisible(R.id.base_read_receipt, false);
        }
    }

    //初始化用户信息
    private void initUserInfo(
            final ViewHolder holder,
            final UiMessage uiMessage,
            final int position,
            final IViewProviderListener<UiMessage> listener,
            boolean isSender) {
        if (mConfig.showPortrait) {
            holder.setVisible(R.id.base_left_avatar, !isSender);
            holder.setVisible(R.id.base_right_avatar, isSender);
            //头像
            IAvatarView headImage = holder.getView(isSender ? R.id.base_right_avatar : R.id.base_left_avatar);
            headImage.setUserInfoByMessageUser(uiMessage.getMessage().getFromUser());
            IMCenter.getInstance().getOptions().getImageLoader().loadChatAvatar(holder.getContext(), headImage, uiMessage.getMessage(), isSender);
            OptionsHelper.updateImageSize(headImage, KtExtKt.dp(headImage, io.im.core.R.dimen.chat_dp38));

            holder.setOnClickListener(
                    R.id.base_left_avatar,
                    v -> listener.onViewClick(v, MessageClickType.USER_PORTRAIT_CLICK, position, uiMessage));

            holder.setOnClickListener(
                    R.id.base_right_avatar,
                    v -> listener.onViewClick(v, MessageClickType.USER_PORTRAIT_CLICK, position, uiMessage));

            holder.setOnLongClickListener(
                    R.id.base_left_avatar,
                    v -> listener.onViewLongClick(v, MessageClickType.USER_PORTRAIT_LONG_CLICK, position, uiMessage));

            holder.setOnLongClickListener(
                    R.id.base_right_avatar,
                    v -> listener.onViewLongClick(v, MessageClickType.USER_PORTRAIT_LONG_CLICK, position, uiMessage));

            if (mConfig.showNickName && uiMessage.getMessage().getConversationType() == ConversationType.PRIVATE) {
                if (isSender) {
                    holder.setVisible(R.id.base_title, false);
                } else {
                    holder.setVisible(R.id.base_title, true);
                    holder.setText(R.id.base_title, uiMessage.getMessage().getFromUser().getName());
                }
            } else {
                //隐藏昵称
                holder.setVisible(R.id.base_title, false);
            }
        } else {
            holder.setVisible(R.id.base_left_avatar, false);
            holder.setVisible(R.id.base_right_avatar, false);
            holder.setVisible(R.id.base_title, false);
        }
    }

    @Override
    public boolean isItemViewType(UiMessage item) {
        return isMessageViewType(item.getMessage().getMessageContent());
    }

    @Override
    public boolean isSummaryType(MessageContent messageContent) {
        return isMessageViewType(messageContent);
    }

    protected abstract ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType);

    protected boolean canEdit() {
        return true;
    }

    protected abstract void bindContentViewHolder(
            ViewHolder parentHolder,
            ViewHolder contentHolder,
            T msgContent,
            UiMessage uiMessage,
            boolean isSender,
            int position,
            List<UiMessage> list,
            IViewProviderListener<UiMessage> listener);

    protected abstract boolean isMessageViewType(@Nullable MessageContent messageContent);

    protected abstract boolean onItemClick(
            ViewHolder holder,
            View view,
            T t,
            UiMessage uiMessage,
            int position,
            List<UiMessage> list,
            IViewProviderListener<UiMessage> listener);

    protected boolean onItemLongClick(
            ViewHolder holder,
            View view,
            T t,
            UiMessage uiMessage,
            int position,
            List<UiMessage> list,
            IViewProviderListener<UiMessage> listener) {
        return false;
    }

    protected boolean checkViewsValid(View... views) {
        if (views == null || views.length == 0) {
            return false;
        }
        for (View view : views) {
            if (view == null) {
                return false;
            }
        }
        return true;
    }

    protected final void log(String m) {
        JLog.e(getClass().getSimpleName(), m);
    }

    public static class MessageViewHolder extends ViewHolder {
        private final ViewHolder mMessageContentViewHolder;
        private UiMessage uiMessage;
        private MessageItemProviderConfig mConfig;

        public MessageViewHolder(Context context, View itemView, ViewHolder messageViewHolder, MessageItemProviderConfig mConfig) {
            super(context, itemView);
            mMessageContentViewHolder = messageViewHolder;
            this.mConfig = mConfig;
        }

        public ViewHolder getMessageContentViewHolder() {
            return mMessageContentViewHolder;
        }

        public UiMessage getUiMessage() {
            return uiMessage;
        }

        public void setUiMessage(UiMessage uiMessage) {
            this.uiMessage = uiMessage;
        }

        public MessageItemProviderConfig getConfig() {
            return mConfig;
        }
    }
}
