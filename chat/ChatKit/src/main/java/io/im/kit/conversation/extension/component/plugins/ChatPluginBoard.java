package io.im.kit.conversation.extension.component.plugins;

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
import io.im.kit.conversation.IConversationFragment;
import io.im.kit.databinding.KitPanelPluginBoardBinding;

/**
 * author : JFZ
 * date : 2024/1/29 09:59
 * description :
 */
public class ChatPluginBoard extends FrameLayout {
    private final KitPanelPluginBoardBinding binding;

    private static final int DEFAULT_SHOW_COLUMN = 4;

    private static final int DEFAULT_SHOW_ROW = 2;

    private final int mPluginCountPerPage = 8;

    private List<ChatPluginModule> pluginModules;

    public ChatPluginBoard(@NonNull Context context) {
        this(context, null);
    }

    public ChatPluginBoard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatPluginBoard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = KitPanelPluginBoardBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    public void initPlugin(IConversationFragment fragment) {
        if (pluginModules != null && pluginModules.size() > 0) {
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
        PluginPagerAdapter mPagerAdapter = new PluginPagerAdapter(pages, count);
        binding.pager.setAdapter(mPagerAdapter);
        binding.pager.setOffscreenPageLimit(1);
    }


    private class PluginPagerAdapter extends RecyclerView.Adapter<PluginPagerAdapter.PluginPagerViewHolder> {
        int pages;
        int items;

        public PluginPagerAdapter(int pages, int items) {
            this.pages = pages;
            this.items = items;
        }

        @NonNull
        @Override
        public PluginPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.kit_plugin_grid_view, parent, false);
            GridView gridView = root.findViewById(R.id.grid_view);
            return new PluginPagerViewHolder(root);
        }

        @Override
        public void onBindViewHolder(@NonNull PluginPagerViewHolder holder, int position) {
            GridView gridView = holder.gridView;
            gridView.setNumColumns(DEFAULT_SHOW_COLUMN);
            gridView.setAdapter(new PluginItemAdapter(position * mPluginCountPerPage, items));
        }

        @Override
        public int getItemCount() {
            return pages;
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

        public PluginItemAdapter(int index, int count) {
            this.count = Math.min(mPluginCountPerPage, count - index);
            this.index = index;
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.kit_plugin_item, null);

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

//            convertView.setBackgroundColor(ChatLibUtil.randomColor());

            holder.imageView.setOnClickListener(v -> {
            });

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
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }


}
