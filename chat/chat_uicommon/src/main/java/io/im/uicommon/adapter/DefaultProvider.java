package io.im.uicommon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.im.uicommon.R;
import io.im.uicommon.providers.IViewProvider;


public class DefaultProvider implements IViewProvider {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_chat_item_message_default, parent, false);
        return new ViewHolder(view.getContext(), view);
    }

    @Override
    public boolean isItemViewType(Object item) {
        return true;
    }

    @Override
    public void bindViewHolder(ViewHolder holder, Object o, int position, List list, IViewProviderListener listener) {
        // do nothing
    }
}
