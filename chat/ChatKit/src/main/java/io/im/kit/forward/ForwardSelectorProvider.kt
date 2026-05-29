package io.im.kit.forward

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import io.im.kit.IMCenter
import io.im.kit.R
import io.im.kit.widget.adapter.IViewProvider
import io.im.kit.widget.adapter.IViewProviderListener
import io.im.kit.widget.adapter.ViewHolder
import io.im.lib.model.UserInfo


/**
 * by DAD FZ
 * 2026/5/29
 * desc：
 **/
class ForwardSelectorProvider : IViewProvider<UserInfo> {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        parent.context,
        LayoutInflater.from(parent.context)
            .inflate(R.layout.forward_item_selector_user, parent, false)
    )

    override fun isItemViewType(item: UserInfo) = true

    override fun bindViewHolder(
        holder: ViewHolder,
        item: UserInfo,
        position: Int,
        list: List<UserInfo>,
        listener: IViewProviderListener<UserInfo>?
    ) {
        holder.setText(R.id.name, item.userName)

        val image = holder.getView<ImageView>(R.id.image)
        IMCenter.getInstance().options.imageLoader.loadForwardSelectorAvatar(
            holder.context,
            image,
            item
        )
    }
}