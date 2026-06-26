package io.im.uicommon.repo

import io.im.core.core.ChatSDK
import io.im.core.listener.FetchCallback
import io.im.core.model.ConversationType
import io.im.core.model.Message
import io.im.core.model.Session
import io.im.core.utils.ChatExecutorHelper
import io.im.core.utils.ConversationIdUtil
import io.im.uicommon.IMCenter


/**
 * by DAD FZ
 * 2026/6/25
 * desc：
 **/
object ConversationRepo {


    /**
     * 创建会话
     * @param message 消息
     * @param updateLocal 如果本地存在该会话，是否更新本地会话最新消息
     * @param callback 回调
     */
    @JvmStatic
    fun createConversation(
        message: Message,
        updateLocal: Boolean,
        callback: FetchCallback<Session>?
    ) {
        val myAccount = IMCenter.getAccountId()
        val toUser = if (message.fromUser.id == myAccount) message.toUser else message.fromUser
        val friendAccount = toUser.id
        ChatExecutorHelper.getInstance().diskIO().execute {
            val sid = ConversationIdUtil.conversationId(friendAccount, message.conversationType)
            var session = ChatSDK.getDbManager().sessionDao().getSessionBySid(sid)
            if (session != null) {
                if (updateLocal) {
                    session.updateTime = message.updateTime
                    session.session = toUser.toJson()
                    session.type = message.conversationType.value
                    session.lastMessage = message.toJson()
                    ChatSDK.getDbManager().sessionDao().update(session)
                }
            } else {
                session = Session.obtain(
                    toUser.toUserInfo(),
                    message.conversationType,
                    message
                )
                ChatSDK.getDbManager().sessionDao().insertSession(session)
            }
            ChatExecutorHelper.getInstance().mainThread().execute {
                callback?.onSuccess(session)
            }
        }
    }

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