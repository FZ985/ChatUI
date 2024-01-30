package io.im.kit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import io.im.kit.widget.switchpanel.PanelSwitchHelper;


public class AutoHidePanelRecyclerView extends RecyclerView {

    PanelSwitchHelper panelSwitchHelper;

    public void setPanelSwitchHelper(PanelSwitchHelper panelSwitchHelper) {
        this.panelSwitchHelper = panelSwitchHelper;
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
            if (panelSwitchHelper != null) {
                panelSwitchHelper.hookSystemBackByPanelSwitcher();
            }
        }
        return super.onTouchEvent(e);
    }
}
