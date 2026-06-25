package io.chat.kit.repo

import io.im.core.core.ChatSDK
import io.im.core.listener.FetchCallback
import io.im.core.model.ConversationType
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
     * 删除聊天消息
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