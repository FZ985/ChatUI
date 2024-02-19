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

    private final List<ChatEmoticonTab> emojiTabs = new ArrayList<>();

    public ChatEmojiConfig() {
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

    public interface AddButtonClickListener {
        void onAddClick(View view);
    }

}
