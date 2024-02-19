package io.im.kit.conversation.messagelist.provider;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import io.im.kit.IMCenter;
import io.im.kit.R;
import io.im.kit.helper.OptionsHelper;
import io.im.kit.model.UiMessage;
import io.im.kit.utils.DateUtil;
import io.im.kit.widget.adapter.IViewProviderListener;
import io.im.kit.widget.adapter.ViewHolder;
import io.im.lib.model.ConversationType;
import io.im.lib.model.Message;
import io.im.lib.model.MessageContent;
import io.im.lib.model.State;
import io.im.lib.utils.JLog;

/**
 * author : JFZ
 * date : 2024/1/27 13:42
 * description :消息总类
 */
public abstract class BaseMessageItemProvider<T extends MessageContent> implements ConversationMessageProvider<T> {

    protected MessageItemProviderConfig mConfig = new MessageItemProviderConfig();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.kit_item_message_base, parent, false);
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
            holder.setVisible(R.id.base_edit_iv, uiMessage.isEdit());
            holder.setVisible(R.id.base_edit, uiMessage.isEdit());
            if (uiMessage.isEdit()) {
                holder.setSelected(R.id.base_edit_iv, uiMessage.isSelected());
                holder.setOnClickListener(
                        R.id.base_edit,
                        v -> listener.onViewClick(v, MessageClickType.EDIT_CLICK, uiMessage));
            }
            initTime(holder, position, list, message);
            initContent(holder, isSender, uiMessage, position, listener, list);
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
        if (mConfig.centerInHorizontal) {
            layout.setGravity(Gravity.CENTER_HORIZONTAL);
        } else {
            layout.setGravity(isSender ? Gravity.END : Gravity.START);
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
                        listener.onViewClick(v, MessageClickType.CONTENT_CLICK, uiMessage);
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
                        return listener.onViewLongClick(v, MessageClickType.CONTENT_LONG_CLICK, uiMessage);
                    }
                    return false;
                });
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
                        v -> listener.onViewClick(v, MessageClickType.WARNING_CLICK, uiMessage));
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
        if (mConfig.showReadState
                && isSender
                && message.getReadStatus() == Message.ReadStatus.UN_READ) {
            holder.setVisible(R.id.base_read_receipt, true);
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
            ImageView headImage = holder.getView(isSender ? R.id.base_right_avatar : R.id.base_left_avatar);
            IMCenter.getInstance().getOptions().getImageLoader().loadConversationAvatar(holder.getContext(), headImage, uiMessage.getMessage(), isSender);
            holder.setOnClickListener(
                    R.id.base_left_avatar,
                    v -> listener.onViewClick(v, MessageClickType.USER_PORTRAIT_CLICK, uiMessage));

            holder.setOnClickListener(
                    R.id.base_right_avatar,
                    v -> listener.onViewClick(v, MessageClickType.USER_PORTRAIT_CLICK, uiMessage));

            holder.setOnLongClickListener(
                    R.id.base_left_avatar,
                    v -> listener.onViewLongClick(v, MessageClickType.USER_PORTRAIT_LONG_CLICK, uiMessage));

            holder.setOnLongClickListener(
                    R.id.base_right_avatar,
                    v -> listener.onViewLongClick(v, MessageClickType.USER_PORTRAIT_LONG_CLICK, uiMessage));

            if (mConfig.showNickName && uiMessage.getMessage().getConversationType() == ConversationType.PRIVATE) {
                if (isSender) {
                    holder.setVisible(R.id.base_title, false);
                } else {
                    holder.setVisible(R.id.base_title, true);
                    holder.setText(R.id.base_title, uiMessage.getMessage().getToName());
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
