package io.chat.conversation.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import io.chat.conversation.R
import io.chat.conversation.model.UiSession
import io.chat.conversation.utils.ConUIHelper
import io.chat.kit.chat.messagelist.provider.MessageClickType
import io.chat.kit.provider.ChatProvider
import io.im.core.model.Session
import io.im.uicommon.IMCenter
import io.im.uicommon.adapter.IViewProvider
import io.im.uicommon.adapter.IViewProviderListener
import io.im.uicommon.adapter.ViewHolder
import io.im.uicommon.helper.OptionsHelper
import io.im.uicommon.utils.DateUtil
import io.im.uicommon.utils.doClick
import io.im.uicommon.utils.dp
import io.im.uicommon.widgets.IAvatarView


/**
 * by DAD FZ
 * 2026/5/29
 * desc：
 **/
class ConversationProvider : IViewProvider<UiSession> {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        parent.context,
        LayoutInflater.from(parent.context)
            .inflate(R.layout.con_item_conversation, parent, false)
    )

    override fun isItemViewType(item: UiSession) = true

    override fun bindViewHolder(
        holder: ViewHolder,
        item: UiSession,
        position: Int,
        list: List<UiSession>,
        listener: IViewProviderListener<UiSession>?
    ) {
        //头像
        val image = holder.getView<IAvatarView>(R.id.image)
        image.setUserInfo(item.user)
        IMCenter.getInstance().options.imageLoader.loadConversationAvatar(
            holder.context,
            image,
            item.session
        )
        OptionsHelper.updateImageSize(image, image.dp(io.im.core.R.dimen.chat_dp48))

        //时间
        val time = holder.getView<TextView>(R.id.time)
        time.text = DateUtil.getDateTimeString(item.lastMsgTime, false, holder.context)
        OptionsHelper.updateTextSize(time, 11)

        //名称
        val name = holder.getView<TextView>(R.id.name)
        name.text = item.session.name
        OptionsHelper.updateTextSize(name, 16)

        //免打扰
        val disturb = holder.getView<ImageView>(R.id.disturb)
        OptionsHelper.updateImageSize(disturb, disturb.dp(io.im.core.R.dimen.chat_dp14))

        //内容
        val content = holder.getView<TextView>(R.id.content)
        content.text = getContent(holder.context, item.session)
        OptionsHelper.updateTextSize(content, 12)

        //未读数
        val readCount = holder.getView<TextView>(R.id.un_read_count)
        OptionsHelper.updateTextSize(readCount, 13)
        ConUIHelper.updateConversationCount(readCount, item)
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

    private fun getContent(context: Context, session: Session): Spannable {
        if (session.lastMessageObj != null) {
            val message = session.lastMessageObj
            return ChatProvider.getOptions().chatConfig.getMessageSummary(
                context,
                message!!.messageContent
            )
        }
        return SpannableString("")
    }
}