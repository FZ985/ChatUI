package io.im.kit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import io.im.kit.R;
import io.im.kit.helper.OptionsHelper;
import io.im.lib.callback.ChatFun;


/**
 * author : JFZ
 * date : 2023/12/9 11:11
 * description :
 */
public class ChatToolBar extends Toolbar {

    private LinearLayout root;
    private ImageView left;
    private TextView title;

    public ChatToolBar(@NonNull Context context) {
        this(context, null);
    }

    public ChatToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addView(LayoutInflater.from(context).inflate(R.layout.kit_toolbar, this, false));
        root = findViewById(R.id.root);
        left = findViewById(R.id.left);
        title = findViewById(R.id.title);
    }

    public void setLeftOnclick(ChatFun.Fun1<View> onclick) {
        if (left != null) {
            left.setOnClickListener(v -> {
                if (onclick != null) {
                    onclick.apply(v);
                }
            });
        }
    }

    public void setTitleName(String name) {
        if (title != null) {
            title.setText(name);
            updateTitleSize();
        }
    }

    public void updateTitleSize() {
        OptionsHelper.updateTextSize(title, 16);
    }
}
