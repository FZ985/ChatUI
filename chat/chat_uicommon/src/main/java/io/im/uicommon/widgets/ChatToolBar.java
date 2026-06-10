package io.im.uicommon.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;

import io.im.core.listener.ChatFun;
import io.im.uicommon.R;
import io.im.uicommon.helper.OptionsHelper;
import io.im.uicommon.widgets.text.selection.SelectableTextHelper;


/**
 * author : JFZ
 * date : 2023/12/9 11:11
 * description :
 */
public class ChatToolBar extends Toolbar {

    private RelativeLayout root;
    private ImageView left;
    private TextView title;

    private View line;

    public ChatToolBar(@NonNull Context context) {
        this(context, null);
    }

    public ChatToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addView(LayoutInflater.from(context).inflate(R.layout.common_kit_toolbar, this, false));
        root = findViewById(R.id.root);
        left = findViewById(R.id.left);
        title = findViewById(R.id.title);
        line = findViewById(R.id.tool_bar_line);
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

    public void setTitleRes(@StringRes int res) {
        if (title != null) {
            title.setText(res);
            updateTitleSize();
        }
    }

    public void setLeftIcon(@DrawableRes int res) {
        if (left != null) {
            left.setImageResource(res);
        }
    }

    public void hideLeft() {
        if (left != null) {
            left.setVisibility(GONE);
        }
    }

    public void showLine(boolean show) {
        if (line != null) {
            if (show) {
                line.setVisibility(VISIBLE);
            } else {
                line.setVisibility(GONE);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        SelectableTextHelper.getInstance().dismiss();
        return super.onInterceptTouchEvent(ev);
    }

    public void updateTitleSize() {
        OptionsHelper.updateTextSize(title, 18);
    }
}
