package io.chat.kit.processor

import io.im.core.core.ChatSDK
import io.im.core.listener.ChatFun
import io.im.core.model.Message
import io.im.core.model.UserInfo
import io.im.core.utils.ChatExecutorHelper
import io.im.core.utils.ConversationIdUtil
import io.im.core.utils.JLog
import io.im.uicommon.IMCenter
import java.util.Collections


/**
 * by DAD FZ
 * 2026/6/24
 * desc：单聊获取消息处理
 **/
class P2PChatMessageProcessor : ChatMessageProcessor() {

    override fun getFirstMessage(
        user: UserInfo,
        call: ChatFun.Fun1<MutableList<Message>>
    ) {
        ChatExecutorHelper.getInstance().diskIO().execute {
            val count = ChatSDK.getDbManager().messageDao().count
            JLog.e("count===:"+count)
            val list = ChatSDK.getDbManager().messageDao()
                .getP2PFirstPageMessageByFromTo(IMCenter.getAccountId(), user.id, mPageSize)
            Collections.reverse(list)
            if (list.isNotEmpty()) {
                lastMessageId = list[0].messageId
            }
            ChatExecutorHelper.getInstance().mainThread().execute {
                call.apply(list)
            }
        }
    }

    override fun loadMoreMessage(
        user: UserInfo,
        call: ChatFun.Fun1<MutableList<Message>>
    ) {
        ChatExecutorHelper.getInstance().diskIO().execute {
            val list = ChatSDK.getDbManager().messageDao()
                .getP2PNextPageMessageByFromTo(
                    IMCenter.getAccountId(),
                    user.id,
                    lastMessageId,
                    mPageSize
                )
            Collections.reverse(list)
            if (list.isNotEmpty()) {
                lastMessageId = list[0].messageId
            }
            ChatExecutorHelper.getInstance().mainThread().execute {
                call.apply(list)
            }
        }
    }

    override fun insertMessage(
        message: Message,
        call: ChatFun.Fun1<Long>
    ) {
        ChatExecutorHelper.getInstance().diskIO().execute {
            val index = ChatSDK.getDbManager().messageDao().insertMessage(message)
            call.apply(index)
        }
    }

    override fun updateMessage(
        message: Message,
        call: ChatFun.Fun?
    ) {
        ChatExecutorHelper.getInstance().diskIO().execute {
            ChatSDK.getDbManager().messageDao().updateMessage(message)
            call?.apply()
        }
    }

    override fun deleteMessage(user: UserInfo, message: Message) {
        deleteMessages(user, mutableListOf(message))
    }

    override fun deleteMessages(user: UserInfo, messages: MutableList<Message>) {
        if (messages.isEmpty()) return
        ChatExecutorHelper.getInstance().diskIO().execute {
            val index = ChatSDK.getDbManager().messageDao()
                .deleteP2PMessages(
                    IMCenter.getAccountId(),
                    user.id,
                    messages.map { it.messageId }.toMutableList()
                )
            val sid = ConversationIdUtil.p2pConversationId(user.id)
            val lastMessage = ChatSDK.getDbManager().messageDao()
                .getLatestP2PMessage(IMCenter.getAccountId(), user.id)
            IMCenter.getInstance().options.onLocalMessageOperateListener.onDeletedAfterLastMessage(
                sid,
                lastMessage
            )
            JLog.e("===delete===message:$index")
        }
    }

    override fun clearMessage(user: UserInfo) {
        ChatExecutorHelper.getInstance().diskIO().execute {
            val index = ChatSDK.getDbManager().messageDao()
                .clearP2PMessages(IMCenter.getAccountId(), user.id)
            val sid = ConversationIdUtil.p2pConversationId(user.id)
            IMCenter.getInstance().options.onLocalMessageOperateListener.onDeletedAfterLastMessage(
                sid,
                null
            )
            JLog.e("===clear===message:$index")
        }
    }
}