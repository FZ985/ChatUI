package io.im.kit.conversation.extension.component.emoticon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.im.kit.IMCenter;
import io.im.kit.R;
import io.im.kit.config.ChatEmojiConfig;
import io.im.kit.conversation.IConversationFragment;
import io.im.kit.conversation.extension.ChatExtensionViewModel;
import io.im.kit.databinding.KitPanelEmojiBoardBinding;
import io.im.kit.widget.adapter.ViewHolder;
import io.im.lib.base.ChatPageAdapter;
import io.im.lib.callback.ChatFun;
import io.im.lib.utils.ChatNull;

/**
 * author : JFZ
 * date : 2024/2/19 13:42
 * description :
 */
public class ChatEmoticonBoard extends LinearLayout {

    private final KitPanelEmojiBoardBinding binding;

    private List<ChatEmoticonTab> emojiTabs = new ArrayList<>();

    private final HashMap<Integer, View> map = new HashMap<>();

    private EmojiTabAdapter tabAdapter;

    private ChatExtensionViewModel mExtensionViewModel;

    private IConversationFragment mFragment;

    public ChatEmoticonBoard(Context context) {
        this(context, null);
    }

    public ChatEmoticonBoard(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatEmoticonBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = KitPanelEmojiBoardBinding.inflate(LayoutInflater.from(getContext()), this, true);
        binding.emojiTab.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void initEmoji(IConversationFragment fragment) {
        if (fragment == null) return;
        this.mFragment = fragment;
        if (emojiTabs.size() > 0) {
            return;
        }
        if (mExtensionViewModel == null) {
            mExtensionViewModel = new ViewModelProvider(fragment).get(ChatExtensionViewModel.class);
        }
        initContent();
    }

    private void initContent() {
        ChatEmojiConfig emojiConfig = IMCenter.getInstance().getOptions().getEmojiConfig();
        boolean showTabItem = emojiConfig.isShowTabItem();
        if (showTabItem) {
            binding.emojiTab.setVisibility(VISIBLE);
            binding.emojiLine.setVisibility(VISIBLE);
        } else {
            binding.emojiTab.setVisibility(GONE);
            binding.emojiLine.setVisibility(GONE);
        }
        boolean showAddButton = emojiConfig.isShowAddButton();
        if (showAddButton) {
            binding.kitEmojiAddButton.setVisibility(VISIBLE);
            binding.kitEmojiAddButton.setOnClickListener(v -> emojiConfig.getAddButtonClickListener().onAddClick(v, this::initContent));
        } else {
            binding.kitEmojiAddButton.setVisibility(GONE);
        }
        emojiTabs = ChatNull.compatList(emojiConfig.getEmojiTabs());
        tabAdapter = new EmojiTabAdapter(emojiTabs);
        binding.emojiTab.setAdapter(tabAdapter);
        tabAdapter.setItemClickListener((v, position) -> {
            binding.emojiVp.setCurrentItem(position);
            tabAdapter.selectIndex(position);
        });
        binding.emojiVp.setAdapter(new EmojiContentAdapter(emojiTabs));
        binding.emojiVp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabAdapter.selectIndex(position);
            }
        });

        if (emojiTabs != null && emojiTabs.size() > 0) {
            for (ChatEmoticonTab tab : emojiTabs) {
                if (tab.getEditInfo() != null) {
                    tab.getEditInfo().removeObserver(mObs);
                    tab.getEditInfo().observe(mFragment, mObs);
                }
            }
        }
    }


    private final Observer<String> mObs = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            if (mFragment.isDetached()) return;
            if (mExtensionViewModel == null) return;
            if (mExtensionViewModel.getEditText() == null) return;
            if (s.equals(ChatEmojiTab.DELETE)) {
                mExtensionViewModel.getEditText().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                int start = mExtensionViewModel.getEditText().getSelectionStart();
                mExtensionViewModel.getEditText().getText().insert(start, s);
            }
        }
    };

    private class EmojiContentAdapter extends ChatPageAdapter<ChatEmoticonTab> {
        public EmojiContentAdapter(List<ChatEmoticonTab> data) {
            super(data);
        }

        @Override
        public View createView(@NonNull Context context, @NonNull ViewGroup parent, int position, ChatEmoticonTab item) {
            if (map.containsKey(position) && map.get(position) != null) {
                return map.get(position);
            }
            View view = item.onCreateTabPager(context);
            map.put(position, view);
            return view;
        }
    }

    public static class EmojiTabAdapter extends RecyclerView.Adapter<ViewHolder> {
        List<ChatEmoticonTab> emojiTabs;
        private int index = 0;

        private ChatFun.Fun2<View, Integer> itemClickListener;

        public EmojiTabAdapter(List<ChatEmoticonTab> emojiTabs) {
            this.emojiTabs = emojiTabs;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kit_emoji_tab_item, parent, false);
            return new ViewHolder(view.getContext(), view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ImageView yl_image = holder.getView(R.id.yl_image);
            ChatEmoticonTab tab = emojiTabs.get(position);
            if (position == index) {
                yl_image.setImageDrawable(tab.onTabSelectDrawable(holder.getContext()));
            } else {
                yl_image.setImageDrawable(tab.onTabUnSelectDrawable(holder.getContext()));
            }
            holder.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.apply(v, position);
                }
            });
        }

        @SuppressLint("NotifyDataSetChanged")
        public void selectIndex(int pos) {
            this.index = pos;
            notifyDataSetChanged();
        }

        public void setItemClickListener(ChatFun.Fun2<View, Integer> itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public int getItemCount() {
            return emojiTabs == null ? 0 : emojiTabs.size();
        }
    }
}
