package io.im.kit.conversation.extension.component.plugins;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * author : JFZ
 * date : 2024/1/29 09:59
 * description :
 */
public class PluginPanel extends FrameLayout {
    public PluginPanel(@NonNull Context context) {
        this(context, null);
    }

    public PluginPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PluginPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void goneAll() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(INVISIBLE);
        }
    }
}
