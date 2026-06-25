package io.chat.kit.repo

import io.im.core.core.ChatSDK
import io.im.core.listener.FetchCallback
import io.im.core.model.ConversationType
import io.im.core.model.Session
import io.im.core.utils.ChatExecutorHelper
import io.im.core.utils.ConversationIdUtil


/**
 * by DAD FZ
 * 2026/6/25
 * desc：
 **/
object ConversationRepo {


    /**
     * 设置会话置顶
     * @param conversationId 会话ID
     * @param stickTop 是否置顶
     * @param callback 回调
     */
    @JvmStatic
    fun setStickTop(conversationId: String, stickTop: Boolean, callback: FetchCallback<Session>?) {
        ChatExecutorHelper.getInstance().diskIO().execute {
            val session = ChatSDK.getDbManager().sessionDao().getSessionBySid(conversationId)
            session?.let {
                it.isTop = if (stickTop) 1 else 0
                ChatSDK.getDbManager().sessionDao().update(session)
                ChatExecutorHelper.getInstance().mainThread().execute {
                    callback?.onSuccess(it)
                }
            }

        }
    }


    /**
     * 删除会话
     * @param conversationId 会话ID
     * @param clearMessage 是否清空消息
     * @param callback 结果回调
     */
    @JvmStatic
    fun deleteConversation(
        conversationId: String,
        clearMessage: Boolean,
        type: ConversationType,
        callback: FetchCallback<Void>?
    ) {
        ChatExecutorHelper.getInstance().threadPool().execute {
            ChatSDK.getDbManager().sessionDao().deleteById(conversationId)
            val id = ConversationIdUtil.conversationToAccountId(conversationId)
            ChatRepo.clearMessage(id, type, object : FetchCallback<Void> {
                override fun onError(errorCode: Int, errorMsg: String?) {
                    callback?.onError(errorCode, errorMsg)
                }

                override fun onSuccess(data: Void?) {
                    callback?.onSuccess(null)
                }
            })
        }
    }


}