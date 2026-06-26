package io.chat.kit.processor

import io.im.core.core.ChatSDK
import io.im.core.listener.ChatFun
import io.im.core.listener.FetchCallback
import io.im.core.model.ConversationType
import io.im.core.model.Message
import io.im.core.model.UserInfo
import io.im.core.utils.ChatExecutorHelper
import io.im.uicommon.IMCenter
import io.im.uicommon.repo.ChatRepo
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

    override fun clearMessage(user: UserInfo) {
        ChatRepo.clearMessage(user.id, ConversationType.TYPE_P2P, object : FetchCallback<Void> {
            override fun onError(errorCode: Int, errorMsg: String?) {

            }

            override fun onSuccess(data: Void?) {

            }
        })
    }
}