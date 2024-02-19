package io.im.kit.conversation.extension.component.emoticon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.im.kit.databinding.KitPanelEmojiBoardBinding;
import io.im.kit.widget.FixedLinearLayoutManager;

/**
 * author : JFZ
 * date : 2024/2/19 13:42
 * description :
 */
public class ChatEmoticonBoard extends LinearLayout {

    private KitPanelEmojiBoardBinding binding;

    private List<ChatEmoticonTab> emojiTabs = new ArrayList<>();

    private final HashMap<Integer, View> map = new HashMap<>();

    public ChatEmoticonBoard(Context context) {
        this(context, null);
    }

    public ChatEmoticonBoard(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatEmoticonBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = KitPanelEmojiBoardBinding.inflate(LayoutInflater.from(context), this, false);
        binding.emojiTab.setLayoutManager(new FixedLinearLayoutManager(context, FixedLinearLayoutManager.HORIZONTAL, false));
        initEmoji();
    }

    private void initEmoji() {
    }
}
