package io.chat.kit.processor

import io.im.core.core.ChatSDK
import io.im.core.listener.ChatFun
import io.im.core.listener.FetchCallback
import io.im.core.model.ConversationType
import io.im.core.model.Message
import io.im.core.model.UserInfo
import io.im.core.utils.ChatExecutorHelper
import io.im.uicommon.repo.ChatRepo
import java.util.Collections


/**
 * by DAD FZ
 * 2026/6/24
 * desc：
 **/
class TeamChatMessageProcessor : ChatMessageProcessor() {
    override fun getFirstMessage(
        user: UserInfo,
        call: ChatFun.Fun1<MutableList<Message>>
    ) {
        ChatExecutorHelper.getInstance().diskIO().execute {
            val list = ChatSDK.getDbManager().messageDao()
                .getTeamFirstPageMessageById(
                    user.id,
                    ConversationType.TYPE_TEAM.value,
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

    override fun loadMoreMessage(
        user: UserInfo,
        call: ChatFun.Fun1<MutableList<Message>>
    ) {
        ChatExecutorHelper.getInstance().diskIO().execute {
            val list = ChatSDK.getDbManager().messageDao()
                .getTeamNextPageMessageById(
                    user.id,
                    ConversationType.TYPE_TEAM.value,
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
        ChatRepo.clearMessage(user.id, ConversationType.TYPE_TEAM, object : FetchCallback<Void> {
            override fun onError(errorCode: Int, errorMsg: String?) {

            }

            override fun onSuccess(data: Void?) {

            }
        })
    }
}