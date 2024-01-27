package io.im.lib.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ChatFragmentPageAdapter extends FragmentPagerAdapter {
    private final List<ChatBaseFragment> fragments;

    public ChatFragmentPageAdapter(@NonNull FragmentManager fm, List<ChatBaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public ChatBaseFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

}
