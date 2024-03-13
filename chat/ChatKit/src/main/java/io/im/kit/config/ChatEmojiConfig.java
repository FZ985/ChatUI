package io.im.kit.config;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.im.kit.conversation.extension.component.emoticon.ChatEmojiTab;
import io.im.kit.conversation.extension.component.emoticon.ChatEmoticonTab;


/**
 * author : JFZ
 * date : 2023/12/14 14:50
 * description :
 */
public class ChatEmojiConfig {

    private AddButtonClickListener addButtonClickListener;

    private boolean showTabItem = true;//是否展示 表情 item 列表

    private boolean showAddButton = true;//是否展示 添加按钮

    private final List<ChatEmoticonTab> emojiTabs = new ArrayList<>();

    public ChatEmojiConfig() {
        emojiTabs.add(new ChatEmojiTab());
        emojiTabs.add(new ChatEmojiTab());
    }

    public List<ChatEmoticonTab> getEmojiTabs() {
        return emojiTabs;
    }

    public void addEmojiTab(ChatEmoticonTab emoticonTab) {
        emojiTabs.add(emoticonTab);
    }

    public AddButtonClickListener getAddButtonClickListener() {
        return addButtonClickListener;
    }

    public void setAddButtonClickListener(AddButtonClickListener addButtonClickListener) {
        this.addButtonClickListener = addButtonClickListener;
    }

    public boolean isShowTabItem() {
        return showTabItem;
    }

    public void setShowTabItem(boolean showTabItem) {
        this.showTabItem = showTabItem;
    }

    public boolean isShowAddButton() {
        return showAddButton && addButtonClickListener != null;
    }

    public void setShowAddButton(boolean showAddButton) {
        this.showAddButton = showAddButton;
    }

    public interface AddButtonClickListener {
        void onAddClick(View view, RefreshEmojiCall refreshEmoji);
    }

    public interface RefreshEmojiCall {
        void refreshUI();
    }

}
