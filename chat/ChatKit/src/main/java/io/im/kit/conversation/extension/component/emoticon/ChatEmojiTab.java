package io.im.kit.conversation.extension.component.emoticon;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.im.kit.R;
import io.im.kit.databinding.KitEmojiTabDefaultBinding;
import io.im.kit.widget.adapter.ViewHolder;
import io.im.lib.callback.ChatFun;
import io.im.lib.utils.ChatLibUtil;
import io.im.lib.utils.ChatNull;
import io.im.lib.utils.JLog;


/**
 * author : JFZ
 * date : 2023/12/14 14:47
 * description : 内置表情
 */
public class ChatEmojiTab implements ChatEmoticonTab {

    static final String DELETE = "delete";

    private final int spanCount = 9;

    private KitEmojiTabDefaultBinding binding;

    private GridLayoutManager layoutManager;

    private final MutableLiveData<String> mEmojiLiveData = new MutableLiveData<>();

    private final View.OnLayoutChangeListener changeListener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> onScrollEvent();


    private final RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            onScrollEvent();
        }
    };

    @Override
    public Drawable onTabSelectDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.kit_emoji_def_select);
    }

    @Override
    public Drawable onTabUnSelectDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.kit_emoji_def_unselect);
    }

    @Override
    public View onCreateTabPager(Context context) {
        binding = KitEmojiTabDefaultBinding.inflate(LayoutInflater.from(context), null, false);

        binding.ylEmojiDel.setOnClickListener(v -> mEmojiLiveData.setValue(DELETE));

        layoutManager = new GridLayoutManager(context, spanCount);
        binding.ylRecycler.setLayoutManager(layoutManager);

        binding.ylRecycler.removeOnLayoutChangeListener(changeListener);
        binding.ylRecycler.addOnLayoutChangeListener(changeListener);

        binding.ylRecycler.removeOnScrollListener(scrollListener);
        binding.ylRecycler.addOnScrollListener(scrollListener);

        EmojiListAdapter adapter = new EmojiListAdapter(spanCount);
        binding.ylRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(index -> {
            try {
                int code = ChatAndroidEmoji.getEmojiCode(index);
                char[] chars = Character.toChars(code);
                StringBuilder key = new StringBuilder(Character.toString(chars[0]));
                for (int i = 1; i < chars.length; i++) {
                    key.append(chars[i]);
                }
                mEmojiLiveData.setValue(key.toString());
            } catch (Exception e) {
                JLog.e("====emoji item click err:" + e.getMessage());
            }
        });

        binding.ylRecycler.post(() -> layoutManager.scrollToPositionWithOffset(0, 0));
        return binding.getRoot();
    }

    private void onScrollEvent() {
        if (binding != null && layoutManager != null) {
            int first = layoutManager.findFirstVisibleItemPosition();
            int last = layoutManager.findLastVisibleItemPosition();
            if (binding.ylEmojiRoot.getVisibility() != View.VISIBLE) {
                return;
            }
            Rect controllAreaRect = new Rect();
            binding.ylEmojiRoot.getGlobalVisibleRect(controllAreaRect);
            for (int i = last; i >= first; i--) {
                View view = layoutManager.findViewByPosition(i);
                if (view == null) {
                    continue;
                }
                ImageView imageView = view.findViewById(R.id.yl_image);
                if (imageView == null) {
                    continue;
                }
                Rect childRect = new Rect();
                imageView.getGlobalVisibleRect(childRect);
                if (controllAreaRect.contains(childRect) || Rect.intersects(controllAreaRect, childRect)) {
                    Rect tempRect = new Rect(controllAreaRect);
                    tempRect.intersect(childRect);
                    double alpha = (Math.sqrt(tempRect.width()) + Math.sqrt(tempRect.height())) / (Math.sqrt(childRect.height()) + Math.sqrt(childRect.width()));
                    imageView.setAlpha(1 - (float) alpha);
                } else {
                    imageView.setAlpha(1.0f);
                }
            }
        }
    }

    private static class EmojiListAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ChatFun.Fun1<Integer> clickListener;
        private final int spanCount;

        public EmojiListAdapter(int spanCount) {
            this.spanCount = spanCount;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kit_emoji_tab_default_item, null, false);
            return new ViewHolder(parent.getContext(), view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int dp15 = ChatLibUtil.dip2px(holder.getContext(), 15);
            RelativeLayout yl_rl_root = holder.getView(R.id.yl_rl_root);
            int count = getItemCount();
            // 计算总行数
            int rowCount = (int) Math.ceil((double) count / spanCount);
            // 计算当前位置所在的行数
            int currentRow = (int) Math.ceil((double) (position + 1) / spanCount);
            // 如果当前行数等于总行数，则为最后一行
            boolean isLastLine = currentRow == rowCount;
            if (isLastLine) {
                yl_rl_root.setPadding(0, dp15, 0, dp15 * 3);
            } else {
                yl_rl_root.setPadding(0, dp15, 0, 0);
            }

            ImageView yl_image = holder.getView(R.id.yl_image);
            yl_image.setImageDrawable(ChatAndroidEmoji.getEmojiDrawable(holder.getContext(), position));
            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.apply(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ChatNull.compatList(ChatAndroidEmoji.getEmojiList()).size();
        }

        public void setOnItemClickListener(ChatFun.Fun1<Integer> clickListener) {
            this.clickListener = clickListener;
        }
    }

    @Override
    public LiveData<String> getEditInfo() {
        return mEmojiLiveData;
    }

}
