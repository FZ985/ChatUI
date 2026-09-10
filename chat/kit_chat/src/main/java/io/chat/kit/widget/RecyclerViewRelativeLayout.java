package io.chat.kit.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import io.im.core.listener.ChatFun;

/**
 * by DAD FZ
 * 2026/9/10
 * desc：
 **/
public class RecyclerViewRelativeLayout extends RelativeLayout {

    ChatFun.Fun touchCall;

    public void setTouchCall(ChatFun.Fun touchCall) {
        this.touchCall = touchCall;
    }


    public RecyclerViewRelativeLayout(Context context) {
        super(context);
    }

    public RecyclerViewRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e != null && e.getAction() != MotionEvent.ACTION_CANCEL) {
            if (touchCall != null) {
                touchCall.apply();
            }
        }
        return super.onTouchEvent(e);
    }
}
