package io.im.lib.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * author : JFZ
 * date : 2023/6/20 11:06
 * description :
 */
public abstract class ChatPageAdapter<T> extends PagerAdapter {

    private List<T> data;

    public ChatPageAdapter(List<T> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    public List<T> getData() {
        return data == null ? new ArrayList<>() : data;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View layout = (View) object;
        container.removeView(layout);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = createView(container.getContext(), container, position, data.get(position));
        container.addView(view);
        return view;
    }

    public abstract View createView(@NonNull Context context, @NonNull ViewGroup parent, int position, T item);
}
