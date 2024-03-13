package io.im.kit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import io.im.lib.callback.ChatFun;


public class AutoHidePanelRecyclerView extends RecyclerView {

    ChatFun.Fun touchCall;

    public void setTouchCall(ChatFun.Fun touchCall) {
        this.touchCall = touchCall;
    }

    public AutoHidePanelRecyclerView(Context context) {
        this(context, null);
    }

    public AutoHidePanelRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public AutoHidePanelRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
