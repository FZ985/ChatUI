package io.im.kit.chat.extension.component.plugins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.im.kit.IMCenter;
import io.im.kit.R;
import io.im.kit.chat.IChatFragment;
import io.im.kit.databinding.ChatPanelPluginBoardBinding;
import io.im.lib.base.ChatBaseFragment;
import io.im.lib.model.Message;

/**
 * author : JFZ
 * date : 2024/1/29 09:59
 * description :
 */
public class ChatPluginBoard extends FrameLayout {
    private final ChatPanelPluginBoardBinding binding;

    private static final int DEFAULT_SHOW_COLUMN = 4;

    private static final int DEFAULT_SHOW_ROW = 2;

    private final int mPluginCountPerPage = 8;

    private List<ChatPluginModule> pluginModules;

    private PluginPagerAdapter mPagerAdapter;

    public ChatPluginBoard(@NonNull Context context) {
        this(context, null);
    }

    public ChatPluginBoard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatPluginBoard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = ChatPanelPluginBoardBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    public void initPlugin(IChatFragment fragment, @Nullable Message replyMessage) {
        if (pluginModules != null && !pluginModules.isEmpty()) {
            if (mPagerAdapter != null) {
                mPagerAdapter.setReplyMessage(replyMessage);
            }
            return;
        }
        pluginModules = IMCenter.getInstance().getOptions().getPluginConfig().getPluginModules(fragment.getUser());
        int pages = 0;
        int count = pluginModules.size();
        if (count > 0) {
            int rem = count % mPluginCountPerPage;
            if (rem > 0) {
                rem = 1;
            }
            pages = count / mPluginCountPerPage + rem;
        }
        mPagerAdapter = new PluginPagerAdapter(fragment, pages, count);
        binding.pager.setAdapter(mPagerAdapter);
        binding.pager.setOffscreenPageLimit(1);
    }


    private class PluginPagerAdapter extends RecyclerView.Adapter<PluginPagerAdapter.PluginPagerViewHolder> {
        int pages;
        int items;
        @Nullable
        Message replyMessage;

        ChatBaseFragment fragment;

        public PluginPagerAdapter(ChatBaseFragment fragment, int pages, int items) {
            this.pages = pages;
            this.items = items;
            this.fragment = fragment;
        }

        @NonNull
        @Override
        public PluginPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_plugin_grid_view, parent, false);
            GridView gridView = root.findViewById(R.id.grid_view);
            return new PluginPagerViewHolder(root);
        }

        @Override
        public void onBindViewHolder(@NonNull PluginPagerViewHolder holder, int position) {
            GridView gridView = holder.gridView;
            gridView.setNumColumns(DEFAULT_SHOW_COLUMN);
            gridView.setAdapter(new PluginItemAdapter(position * mPluginCountPerPage, items, fragment, replyMessage));
        }

        @Override
        public int getItemCount() {
            return pages;
        }


        @SuppressLint("NotifyDataSetChanged")
        public void setReplyMessage(@Nullable Message replyMessage) {
            this.replyMessage = replyMessage;
            notifyDataSetChanged();
        }

        private class PluginPagerViewHolder extends RecyclerView.ViewHolder {
            GridView gridView;

            public PluginPagerViewHolder(@NonNull View itemView) {
                super(itemView);
                this.gridView = itemView.findViewById(R.id.grid_view);
            }
        }
    }

    private class PluginItemAdapter extends BaseAdapter {
        int count;
        int index;
        Pair<Integer, Integer> cellSize;

        @Nullable
        Message replyMessage;

        ChatBaseFragment fragment;

        public PluginItemAdapter(int index, int count, ChatBaseFragment fragment, @Nullable Message replyMessage) {
            this.count = Math.min(mPluginCountPerPage, count - index);
            this.index = index;
            this.replyMessage = replyMessage;
            this.fragment = fragment;
            cellSize = new Pair<>(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Context context = parent.getContext();
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_plugin_item, null);

                holder.imageView = convertView.findViewById(R.id.plugin_icon);
                holder.textView = convertView.findViewById(R.id.plugin_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) convertView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new AbsListView.LayoutParams(cellSize.first, cellSize.second);
            } else {
                layoutParams.width = cellSize.first;
                layoutParams.height = cellSize.second;
            }
            convertView.setLayoutParams(layoutParams);

//            convertView.setOnClickListener(v -> {
//                ChatPluginModule plugin = pluginModules.get(currentPage * mPluginCountPerPage + position);
//                if (mFragment instanceof ChatFragment) {
//                    plugin.onClick(mFragment, ((ChatFragment) mFragment).getExtension(),
//                            currentPage * mPluginCountPerPage + position);
//                }
//            });
//            convertView.setOnLongClickListener(v -> {
//                ChatPluginModule plugin = mPluginModules.get(currentPage * mPluginCountPerPage + position);
//                if (mFragment instanceof ChatFragment) {
//                    return plugin.onLongClick(mFragment, ((ChatFragment) mFragment).getExtension(),
//                            currentPage * mPluginCountPerPage + position);
//                }
//                return false;
//            });

            holder = (ViewHolder) convertView.getTag();
            ChatPluginModule plugin = pluginModules.get(position + index);
            if (plugin == null) {
                return convertView;
            }
            holder.imageView.setImageDrawable(plugin.obtainDrawable(context));
            holder.textView.setText(plugin.obtainTitle(context));
            holder.imageView.setOnClickListener(v -> {
                plugin.onPluginClick(fragment, v, replyMessage);
            });
            holder.imageView.setOnLongClickListener(v -> {
                return plugin.onPluginLongClick(fragment, v, replyMessage);
            });
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }


}
