package io.chat.conversation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import io.chat.conversation.R
import io.im.core.model.Session
import io.im.uicommon.IMCenter
import io.im.uicommon.adapter.IViewProvider
import io.im.uicommon.adapter.IViewProviderListener
import io.im.uicommon.adapter.ViewHolder


/**
 * by DAD FZ
 * 2026/5/29
 * desc：
 **/
class ConversationProvider : IViewProvider<Session> {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        parent.context,
        LayoutInflater.from(parent.context)
            .inflate(R.layout.con_item_conversation, parent, false)
    )

    override fun isItemViewType(item: Session) = true

    override fun bindViewHolder(
        holder: ViewHolder,
        item: Session,
        position: Int,
        list: List<Session>,
        listener: IViewProviderListener<Session>?
    ) {
        val image = holder.getView<ImageView>(R.id.image)

        IMCenter.getInstance().options.imageLoader.loadConversationAvatar(
            holder.context,
            image,
            item
        )
    }
}