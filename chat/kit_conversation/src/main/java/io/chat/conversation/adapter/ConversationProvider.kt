package io.chat.conversation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import io.chat.conversation.R
import io.chat.kit.chat.messagelist.provider.MessageClickType
import io.im.core.model.Session
import io.im.uicommon.IMCenter
import io.im.uicommon.adapter.IViewProvider
import io.im.uicommon.adapter.IViewProviderListener
import io.im.uicommon.adapter.ViewHolder
import io.im.uicommon.helper.OptionsHelper
import io.im.uicommon.utils.doClick
import io.im.uicommon.utils.dp
import io.im.uicommon.widgets.IAvatarView


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
        //头像
        val image = holder.getView<IAvatarView>(R.id.image)
        image.setUserInfo(item.user)
        IMCenter.getInstance().options.imageLoader.loadConversationAvatar(
            holder.context,
            image,
            item
        )
        OptionsHelper.updateImageSize(image, image.dp(io.im.core.R.dimen.chat_dp48))

        //时间
        val time = holder.getView<TextView>(R.id.time)
        time.text = "昨天"
        OptionsHelper.updateTextSize(time, 11)

        //名称
        val name = holder.getView<TextView>(R.id.name)
        name.text = item.friendName
        OptionsHelper.updateTextSize(name, 16)

        //免打扰
        val disturb = holder.getView<ImageView>(R.id.disturb)
        OptionsHelper.updateImageSize(disturb, disturb.dp(io.im.core.R.dimen.chat_dp14))

        //内容
        val content = holder.getView<TextView>(R.id.content)
        content.text = "库克终极绝唱!15亿苹果设备用AI重生"
        OptionsHelper.updateTextSize(content, 12)

        //点击
        val root = holder.getView<ConstraintLayout>(R.id.con_root)
        root.doClick {
            listener?.onViewClick(it, MessageClickType.CONTENT_CLICK, position, item)
        }

        //长按
        root.setOnLongClickListener {
            listener?.onViewLongClick(it, MessageClickType.CONTENT_LONG_CLICK, position, item)
                ?: true
        }
    }
}