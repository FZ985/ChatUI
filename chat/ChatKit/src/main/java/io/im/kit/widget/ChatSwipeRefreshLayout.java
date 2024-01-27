package io.im.kit.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * author : JFZ
 * date : 2024/1/26 14:39
 * description :
 */
public class ChatSwipeRefreshLayout extends SwipeRefreshLayout {
    public ChatSwipeRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public ChatSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

}
