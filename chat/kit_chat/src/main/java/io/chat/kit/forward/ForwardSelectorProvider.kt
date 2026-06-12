package io.chat.kit.forward

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import io.chat.kit.R
import io.im.core.model.UserInfo
import io.im.uicommon.IMCenter
import io.im.uicommon.adapter.IViewProvider
import io.im.uicommon.adapter.IViewProviderListener
import io.im.uicommon.adapter.ViewHolder
import io.im.uicommon.helper.OptionsHelper
import io.im.uicommon.utils.dp
import io.im.uicommon.widgets.IAvatarView


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
        val name = holder.getView<TextView>(R.id.name)
        name.text = item.name
        OptionsHelper.updateTextSize(name, 15)

        val image = holder.getView<IAvatarView>(R.id.image)
        image.setUserInfo(item)
        IMCenter.getInstance().options.imageLoader.loadForwardSelectorAvatar(
            holder.context,
            image,
            item
        )
        OptionsHelper.updateImageSize(image, 40.dp)
    }
}