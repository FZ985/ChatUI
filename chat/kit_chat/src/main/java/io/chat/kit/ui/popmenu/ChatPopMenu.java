package io.chat.kit.ui.popmenu;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.chat.kit.databinding.ChatPopChatMenuBinding;
import io.chat.kit.factory.ChatPopActionFactory;
import io.im.uicommon.widgets.text.selection.SelectableTextHelper;
import io.im.core.core.ChatSDK;
import io.im.core.model.Message;
import io.im.core.model.PluginAction;
import io.im.core.utils.ChatLibUtil;
import io.im.core.utils.JLog;

/**
 * by DAD FZ
 * 2026/5/28
 * desc：
 **/
public class ChatPopMenu {


    private final ChatPopChatMenuBinding binding;

    private static final int DEFAULT_COLUMN_NUM = 5;
    // y offset for pop window
    private static final int Y_OFFSET = 8;

    private final PopupWindow popupWindow;

    private final List<PluginAction> chatPopMenuActionList = new ArrayList<>();

    private final MenuAdapter adapter = new MenuAdapter();

    public ChatPopMenu() {
        binding = ChatPopChatMenuBinding.inflate(LayoutInflater.from(ChatSDK.getContext()));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ChatSDK.getContext(), DEFAULT_COLUMN_NUM);
        binding.menuRecycler.setLayoutManager(gridLayoutManager);
        binding.menuRecycler.setAdapter(adapter);
        binding.bubbleLl.setBubbleColor(ContextCompat.getColor(ChatSDK.getContext(), io.chat.kit.R.color.kit_chat_menu_bubble_color));
        popupWindow = new PopupWindow(
                binding.getRoot(),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(io.chat.kit.R.style.ChatPopAnim);

    }

    /**
     * 初始化文本操作
     *
     * @param text 选中文本
     */
    @SuppressLint("NotifyDataSetChanged")
    private void initStringAction(Context context, Message messageInfo, String text) {
        chatPopMenuActionList.clear();
        chatPopMenuActionList.addAll(ChatPopActionFactory.getInstance().getTextActions(context, messageInfo, text));
        adapter.notifyDataSetChanged();
    }

    /**
     * 弹出pop Window，选中文本时
     *
     * @param anchorView 消息view
     * @param text       选中文本
     * @param isSelf     是否是自己
     */
    public void show(Context context, View anchorView, String text, Message messageInfo, boolean isSelf) {
        initStringAction(context, messageInfo, text);
        if (chatPopMenuActionList.isEmpty()) {
            return;
        }
        showWindow(context, anchorView, isSelf);
    }

    public void show(Context context, View anchorView, Message message, boolean isSender) {
        initDefaultAction(context, message);
        if (chatPopMenuActionList.isEmpty()) {
            return;
        }
        showWindow(context, anchorView, isSender);
    }

    private void showWindow(Context context, View anchorView, boolean isSender) {
        if (popupWindow == null) return;
        log("size:" + chatPopMenuActionList.size()+","+isSender);
        int columnNum = Math.min(chatPopMenuActionList.size(), DEFAULT_COLUMN_NUM);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, columnNum);
        binding.menuRecycler.setLayoutManager(gridLayoutManager);
        // 先测量 popup 大小
        binding.getRoot().measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
        );

        int popupWidth = binding.getRoot().getMeasuredWidth();
        int popupHeight = binding.getRoot().getMeasuredHeight();
        log("popupWidth:" + popupWidth + "," + popupHeight);
        // 获取 anchorView 屏幕坐标
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);

        int anchorX = location[0];
        int anchorY = location[1];

        int anchorWidth = anchorView.getWidth();
        int anchorHeight = anchorView.getHeight();

        // 屏幕尺寸
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        int finalX;
        if (isSender) {
            // 自己消息：右对齐
            finalX = anchorX + anchorWidth - popupWidth;
        } else {
            // 对方消息：左对齐
            finalX = anchorX;
        }

        // 防止超出屏幕
        finalX = Math.max(
                ChatLibUtil.dip2px(context, Y_OFFSET),
                Math.min(finalX, screenWidth - popupWidth - ChatLibUtil.dip2px(context, Y_OFFSET)));

        int margin = ChatLibUtil.dip2px(context, Y_OFFSET);

        boolean showAbove = anchorY > popupHeight + margin + ChatLibUtil.getStatusBarHeight(context);

        int finalY;

        log("showAbove:" + showAbove);

        // anchor 中心点在 popup 内的位置
        float triangleOffset;
        if (isSender) {
            triangleOffset = popupWidth - anchorWidth / 2f;
        } else {
            triangleOffset = anchorWidth / 2f;
        }

        // 防止三角越界
        triangleOffset = Math.max(
                ChatLibUtil.dip2px(context, 20),
                Math.min(
                        triangleOffset,
                        popupWidth - ChatLibUtil.dip2px(context, 20)
                )
        );

        if (showAbove) {
            // 显示在气泡上方
            finalY = anchorY - popupHeight - margin;
            binding.bubbleLl.setBottomOffset(triangleOffset);
        } else {
            // 显示在气泡下方
            finalY = anchorY + anchorHeight + margin;
            // 下方仍超出屏幕则强制上方
            if (finalY + popupHeight > screenHeight) {
                finalY = anchorY - popupHeight - margin;
                binding.bubbleLl.setBottomOffset(triangleOffset);
            }else {
                binding.bubbleLl.setTopOffset(triangleOffset);
            }
        }

        if (isShowing()) {
            popupWindow.update(finalX, finalY, popupWidth, popupHeight);
        } else {
            binding.getRoot().setAlpha(0f);
            popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, finalX, finalY);
            binding.getRoot().animate()
                    .alpha(1f)
                    .setDuration(120)
                    .start();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initDefaultAction(Context context, Message message) {
        chatPopMenuActionList.clear();
        chatPopMenuActionList.addAll(ChatPopActionFactory.getInstance().getMessageActions(context, message));
        adapter.notifyDataSetChanged();
    }

    private PluginAction getChatPopMenuAction(int position) {
        return chatPopMenuActionList.get(position);
    }

    public boolean isShowing() {
        return popupWindow != null && popupWindow.isShowing();
    }

    public void hide() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }


    private void log(String m) {
        JLog.e("Pop", m);
    }

    class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuItemViewHolder> {

        @NonNull
        @Override
        public MenuAdapter.MenuItemViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(io.chat.kit.R.layout.chat_pop_chat_menu_item_layout, parent, false);
            return new MenuItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuAdapter.MenuItemViewHolder holder, int position) {
            PluginAction chatPopMenuAction = getChatPopMenuAction(position);
            if (chatPopMenuAction.getTitleRes() != 0) {
                holder.title.setText(ChatSDK.getContext().getString(chatPopMenuAction.getTitleRes()));
            } else {
                holder.title.setText(chatPopMenuAction.getTitle());
            }
            Drawable drawable = ResourcesCompat.getDrawable(
                    ChatSDK.getContext().getResources(),
                    chatPopMenuAction.getIcon(),
                    null);
            holder.icon.setImageDrawable(drawable);

            holder.itemView.setOnClickListener(
                    v -> {
                        if (chatPopMenuAction.getActionClickListener() != null) {
                            // 文本选择器Dismiss
                            SelectableTextHelper.getInstance().dismiss();
                            chatPopMenuAction.getActionClickListener().onClick(v, chatPopMenuAction.getBean());
                        }
                        hide();
                    });
        }

        @Override
        public int getItemCount() {
            return chatPopMenuActionList.size();
        }

        class MenuItemViewHolder extends RecyclerView.ViewHolder {

            public LinearLayout root_ll;

            public TextView title;
            public ImageView icon;

            public MenuItemViewHolder(@NonNull View itemView) {
                super(itemView);
                root_ll = itemView.findViewById(io.chat.kit.R.id.root_ll);
                title = itemView.findViewById(io.chat.kit.R.id.menu_title);
                icon = itemView.findViewById(io.chat.kit.R.id.menu_icon);
            }
        }
    }
}
