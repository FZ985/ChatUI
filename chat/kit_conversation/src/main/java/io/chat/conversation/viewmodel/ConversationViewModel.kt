package io.chat.conversation.viewmodel

import android.app.Application
import android.os.Looper
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import io.chat.conversation.model.UiSession
import io.chat.kit.ChatRoute
import io.im.core.core.ChatSDK
import io.im.core.model.Message
import io.im.core.model.Session
import io.im.core.utils.ChatExecutorHelper
import io.im.core.utils.ChatLibUtil
import io.im.core.utils.ChatToast
import io.im.core.utils.ConversationIdUtil
import io.im.core.utils.ServeTime
import io.im.uicommon.IMCenter
import io.im.uicommon.UserTest
import io.im.uicommon.event.ChatMessageEvent
import io.im.uicommon.listener.MessageEventListener
import io.im.uicommon.utils.DateUtil


/**
 * by DAD FZ
 * 2026/6/11
 * desc：
 **/
class ConversationViewModel(application: Application) : AndroidViewModel(application),
    MessageEventListener {

    private val mSessionLiveData = MediatorLiveData<MutableList<UiSession>>()

    private val mSessions = mutableListOf<UiSession>()

    init {
        IMCenter.getInstance().getOptions().addMessageEventListener(this)

        ChatExecutorHelper.getInstance().diskIO().execute {
            val list = ChatSDK.getDbManager().sessionDao().allSession()
            ChatExecutorHelper.getInstance().mainThread().execute {
                if (mSessions.isEmpty() && list.isNotEmpty()) {
                    list.forEach {
                        mSessions.add(mapUISession(it))
                    }
                } else {
                    //模拟数据
                    val moniList = buildTestDataSession()
                    mSessions.addAll(moniList)
                    ChatExecutorHelper.getInstance().diskIO().execute {
                        ChatSDK.getDbManager().sessionDao()
                            .insertSessionList(moniList.map { it.session }.toMutableList())
                    }
                }
                refreshAllMessage()
            }
        }
    }

    //创建模拟数据
    private fun buildTestDataSession(): MutableList<UiSession> {
        val list = mutableListOf<UiSession>()
        val users = UserTest.randomUserList()
        users.forEachIndexed { index, user ->
            list.add(mapUISession(Session().apply {
                sid = ConversationIdUtil.p2pConversationId(user.id)
                session = user.toJson()
                isTop = if (index < 3) 1 else 0
                unreadCount = ChatLibUtil.randomNumber(1, 200)
                createTime =
                    if (index < 4) ServeTime.currentTimeMillis() + (index * 1000 * 60) else ServeTime.currentTimeMillis() - (index * DateUtil.DAY)
            }))
        }
        return list
    }


    private fun mapUISession(session: Session): UiSession {
        val uiSession = UiSession(session)
        return uiSession
    }

    fun onViewClick(
        view: View,
        clickType: Int,
        position: Int,
        data: UiSession
    ) {
        ChatRoute.goPrivateChat(view.context, data.user)
    }

    fun onViewLongClick(
        view: View,
        clickType: Int,
        position: Int,
        data: UiSession
    ): Boolean {
        ChatToast.toast(view.context, "long click")
        return true
    }

    override fun onSendMediaMessage(event: ChatMessageEvent) {
        onSendMessage(event)
    }

    override fun onSendMessage(event: ChatMessageEvent) {
        insertOrUpdateConversation(event.message)

    }

    override fun onReceiveMessage(event: ChatMessageEvent) {
        insertOrUpdateConversation(event.message)
    }

    private fun insertOrUpdateConversation(message: Message) {
        val myAccount = IMCenter.getAccountId()
        val friendAccount =
            if (message.fromUser.id == myAccount) message.toUser.id else message.fromUser.id
        ChatExecutorHelper.getInstance().diskIO().execute {
            val sid = ConversationIdUtil.conversationId(friendAccount, message.conversationType)
            var session = ChatSDK.getDbManager().sessionDao().getSessionBySid(sid)
            if (session != null) {
                session.updateTime = message.updateTime
                session.session = message.fromUser.toJson()
                session.type = message.conversationType.value
                session.lastMessage = message.toJson()
                ChatSDK.getDbManager().sessionDao().update(session)
            } else {
                session = Session.obtain(
                    message.fromUser.toUserInfo(),
                    message.conversationType,
                    message
                )
                ChatSDK.getDbManager().sessionDao().insertSession(session)
            }

            var uiSession = findUISessionById(sid)
            val isAdd = uiSession == null
            if (isAdd) {
                uiSession = mapUISession(session)
            } else {
                uiSession.session = session
            }
            ChatExecutorHelper.getInstance().mainThread().execute {
                if (isAdd) {
                    sendSessionEvent(uiSession)
                } else {
                    refreshSingleMessage(uiSession)
                }
            }
        }
    }

    private fun sendSessionEvent(uiSession: UiSession) {
        mSessions.add(uiSession)
        refreshAllMessage()
    }

    private fun findSessionIndexById(sid: String): Int {
        return mSessions.indexOfFirst { it.session.sid == sid }
    }

    private fun findUISessionById(sid: String): UiSession? {
        val index = findSessionIndexById(sid)
        if (index != -1) {
            return mSessions[index]
        }
        return null
    }

    fun refreshAllMessage() {
        refreshAllMessage(true)
    }

    fun refreshAllMessage(force: Boolean) {
        if (force) {
            mSessions.forEach {
                it.change()
            }
        }
        excListLiveData()
    }

    fun refreshSingleMessage(uiSession: UiSession) {
        val position = findSessionIndexById(uiSession.session.sid)
        if (position != -1) {
            uiSession.isChange = true
            excListLiveData()
        }
    }

    private fun excListLiveData() {
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            mSessionLiveData.value = mSessions
        } else {
            mSessionLiveData.postValue(mSessions)
        }
    }

    fun getSessionLiveData(): MediatorLiveData<MutableList<UiSession>> {
        return mSessionLiveData
    }
}