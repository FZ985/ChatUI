package io.chat.kit.widget;


import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import io.im.uicommon.widgets.text.selection.SelectableTextHelper;

/**
 * by DAD FZ
 * 2026/5/28
 * desc：
 **/
public class ChatView extends LinearLayout {
    public ChatView(Context context) {
        super(context);
    }

    public ChatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP
                || ev.getAction() == MotionEvent.ACTION_CANCEL
                || ev.getAction() == MotionEvent.ACTION_MOVE) {

            float x = ev.getRawX();
            float y = ev.getRawY();

            View selectView = SelectableTextHelper.getInstance().getSelectView();
            if (selectView != null
                    && selectView.getVisibility() == VISIBLE
                    && isViewUnder(selectView, x, y)) {
                return super.onInterceptTouchEvent(ev);
            }
            //非选择文本状态下，点击其他区域，隐藏选择框
            SelectableTextHelper.getInstance().dismiss();
        }
        return super.onInterceptTouchEvent(ev);
    }

    //判断点击的位置是否在view内
    private boolean isViewUnder(View view, float x, float y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        RectF rectF = new RectF(viewX, viewY, viewX + view.getWidth(), viewY + view.getHeight());
        return rectF.contains(x, y);
//        return (x > viewX
//                && x < (viewX + view.getWidth())
//                && y > viewY
//                && y < (viewY + view.getHeight()));
    }
}
