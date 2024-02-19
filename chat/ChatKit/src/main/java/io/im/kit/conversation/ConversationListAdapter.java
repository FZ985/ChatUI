package io.im.kit.conversation;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.im.kit.IMCenter;
import io.im.kit.R;
import io.im.kit.conversation.messagelist.provider.BaseMessageItemProvider;
import io.im.kit.model.UiMessage;
import io.im.kit.utils.AnimatedColor;
import io.im.kit.utils.KtExtKt;
import io.im.kit.widget.adapter.BaseAdapter;
import io.im.kit.widget.adapter.IViewProviderListener;
import io.im.lib.model.Message;

/**
 * author : JFZ
 * date : 2024/1/27 11:31
 * description :
 */
public class ConversationListAdapter extends BaseAdapter<UiMessage> {

    MessageDiffCallBack mDiffCallback = new MessageDiffCallBack();

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView != null) {
            AnimatedColor animatedColor = new AnimatedColor(ContextCompat.getColor(recyclerView.getContext(), R.color.kit_bubble_right_color),
                    ContextCompat.getColor(recyclerView.getContext(), R.color.kit_bubble_right_color2));
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    updateItemBg();
                }

                private void updateItemBg() {
                    int count = recyclerView.getChildCount();
                    for (int i = 0; i < count; i++) {
                        RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                        if (holder instanceof BaseMessageItemProvider.MessageViewHolder) {
                            BaseMessageItemProvider.MessageViewHolder messageViewHolder = (BaseMessageItemProvider.MessageViewHolder) holder;
                            UiMessage message = messageViewHolder.getUiMessage();
                            if (message != null
                                    && messageViewHolder.getConfig().showContentBubble
                                    && messageViewHolder.getConfig().showContentBubbleGradient
                                    && message.getMessage().getMessageDirection() == Message.MessageDirection.SEND) {
                                View bgView = messageViewHolder.getView(R.id.base_content);
                                if (bgView != null) {
                                    bgView.post(() -> {
                                        int color = animatedColor.with(getFloatRange(holder.itemView));
                                        LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.kit_bg_bubble_gradient_right);
                                        GradientDrawable gradientDrawable = (GradientDrawable) drawable.findDrawableByLayerId(R.id.bubble_gradient_right);
                                        gradientDrawable.setColor(color);
                                        bgView.setBackground(drawable);
                                    });
                                }
                            }
                        }
                    }
                }

                private float getFloatRange(View view) {
                    return 1f - (KtExtKt.absY(view) / (float) recyclerView.getResources().getDisplayMetrics().heightPixels);
                }
            });
        }
    }

    public ConversationListAdapter(IViewProviderListener<UiMessage> listener) {
        super(listener, IMCenter.getInstance().getOptions().getConversationConfig().getConversationProvider());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void setDataCollection(List<UiMessage> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        // 当有空布局的时候，需要全部刷新
        if ((mDataList.size() == 0 && data.size() > 0)
                || (mDataList.size() > 0 && data.size() == 0)) {
            super.setDataCollection(data);
            notifyDataSetChanged();
        } else {
            mDiffCallback.setNewList(data);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(mDiffCallback, false);
            super.setDataCollection(data);
            diffResult.dispatchUpdatesTo(
                    new ListUpdateCallback() {
                        @Override
                        public void onInserted(int position, int count) {
                            notifyItemRangeInserted(getHeadersCount() + position, count);
                        }

                        @Override
                        public void onRemoved(int position, int count) {
                            notifyItemRangeRemoved(getHeadersCount() + position, count);
                        }

                        @Override
                        public void onMoved(int fromPosition, int toPosition) {
                            notifyItemMoved(getHeadersCount() + fromPosition, getHeadersCount() + toPosition);
                        }

                        @Override
                        public void onChanged(int position, int count, @Nullable Object payload) {
                            notifyItemRangeChanged(getHeadersCount() + position, count, null);
                        }
                    });
        }
    }

    private class MessageDiffCallBack extends DiffUtil.Callback {
        private List<UiMessage> newList;

        @Override
        public int getOldListSize() {
            if (mDataList != null) {
                return mDataList.size();
            } else {
                return 0;
            }
        }

        @Override
        public int getNewListSize() {
            if (newList != null) {
                return newList.size();
            } else {
                return 0;
            }
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mDataList.get(oldItemPosition).getMessageId()
                    == newList.get(newItemPosition).getMessageId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            UiMessage newItem = newList.get(newItemPosition);
            if (newItem.isChange()) {
                return false;
            }
            return true;
        }

        public void setNewList(List<UiMessage> newList) {
            this.newList = newList;
        }
    }
}
