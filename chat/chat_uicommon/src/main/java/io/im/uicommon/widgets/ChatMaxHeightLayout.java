package io.im.uicommon.widgets;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import io.im.core.utils.ChatLibUtil;
import io.im.uicommon.R;


/**
 * by DAD FZ
 * 2026/5/12
 * desc：
 **/
public class ChatMaxHeightLayout extends LinearLayout {

    private int maxHeight = Integer.MAX_VALUE;

    public ChatMaxHeightLayout(Context context) {
        super(context);
    }

    public ChatMaxHeightLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int max = getScreenHeight(context) - (ChatLibUtil.dip2px(context, 213) + 65);
        // 读取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChatMaxHeightLayout);
        maxHeight = ta.getDimensionPixelSize(
                R.styleable.ChatMaxHeightLayout_max_height,
                max
        );
        ta.recycle();
    }

    public ChatMaxHeightLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(heightMeasureSpec);
        // 限制最大高度
        if (size > maxHeight) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    maxHeight,
                    MeasureSpec.AT_MOST
            );
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int getScreenHeight(Context context) {
        return getMetrics(context).heightPixels;
    }

    private DisplayMetrics getMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        requestLayout();
    }
}
