package io.im.uicommon.repo

import io.im.core.core.ChatSDK
import io.im.core.listener.FetchCallback
import io.im.core.model.ConversationType
import io.im.core.model.Message
import io.im.core.utils.ChatExecutorHelper
import io.im.core.utils.ConversationIdUtil
import io.im.uicommon.IMCenter


/**
 * by DAD FZ
 * 2026/6/25
 * desc：
 **/
object ChatRepo {

    /**
     * 更新本地消息
     * @param message 消息
     * @param callback 回调
     */
    @JvmStatic
    fun updateLocalMessage(message: Message, callback: FetchCallback<Void>) {
        ChatExecutorHelper.getInstance().diskIO().execute {
            ChatSDK.getDbManager().messageDao().updateMessage(message)
            ChatExecutorHelper.getInstance().mainThread().execute {
                callback.onSuccess(null)
            }
        }
    }

    /**
     * 插入消息到本地
     * @param message 消息
     * @param callback 回调
     */
    @JvmStatic
    fun insertLocalMessage(message: Message, callback: FetchCallback<Long>?) {
        ChatExecutorHelper.getInstance().diskIO().execute {
            val index = ChatSDK.getDbManager().messageDao().insertMessage(message)
            ChatExecutorHelper.getInstance().mainThread().execute {
                callback?.onSuccess(index)
            }
        }
    }

    /**
     * 删除聊天消息
     * @param messages 消息
     * @param toId 目标id
     * @param callback 回调
     */
    @JvmStatic
    fun deleteMessages(
        messages: MutableList<Message>,
        toId: String,
        callback: FetchCallback<Int>
    ) {
        if (messages.isEmpty()) return
        ChatExecutorHelper.getInstance().diskIO().execute {
            val index = ChatSDK.getDbManager().messageDao()
                .deleteMessages(messages.map { it.messageId }.toMutableList())
            //更新会话列表的最新消息
            val sid = ConversationIdUtil.p2pConversationId(toId)
            val lastMessage = ChatSDK.getDbManager().messageDao()
                .getLatestP2PMessage(IMCenter.getAccountId(), toId)
            IMCenter.getInstance().options.onLocalMessageOperateListener.onDeletedAfterLastMessage(
                sid,
                lastMessage
            )
            ChatExecutorHelper.getInstance().mainThread().execute {
                callback.onSuccess(index)
            }
//            JLog.e("===delete===message:$index")
        }
    }

    /**
     * 清空聊天消息
     * @param id 聊天id
     * @param callback 结果回调
     */
    @JvmStatic
    fun clearMessage(
        id: String,
        type: ConversationType,
        callback: FetchCallback<Void>?
    ) {
        ChatExecutorHelper.getInstance().threadPool().execute {
            val dao = ChatSDK.getDbManager().messageDao()
            if (type == ConversationType.TYPE_P2P) {
                dao.clearP2PMessages(IMCenter.getAccountId(), id)
            }
            if (type == ConversationType.TYPE_TEAM) {
                dao.clearTeamMessages(id, ConversationType.TYPE_TEAM.value)
            }
            ChatExecutorHelper.getInstance().mainThread().execute {
                val sid = ConversationIdUtil.p2pConversationId(id)
                IMCenter.getInstance().options.onLocalMessageOperateListener.onDeletedAfterLastMessage(
                    sid,
                    null
                )
                callback?.onSuccess(null)
            }
        }
    }


}