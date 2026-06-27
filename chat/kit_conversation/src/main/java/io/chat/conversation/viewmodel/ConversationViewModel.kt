package io.chat.conversation.viewmodel

import android.app.Application
import android.os.Looper
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import io.chat.conversation.utils.ConversationUtil
import io.im.core.core.ChatSDK
import io.im.core.listener.ChatLifecycle
import io.im.core.listener.FetchCallback
import io.im.core.model.ConversationType
import io.im.core.model.Message
import io.im.core.model.Session
import io.im.core.utils.ChatExecutorHelper
import io.im.core.utils.ServeTime
import io.im.uicommon.IMCenter
import io.im.uicommon.event.ChatMessageEvent
import io.im.uicommon.event.PageEvent
import io.im.uicommon.event.ScrollToTopEvent
import io.im.uicommon.listener.MessageEventListener
import io.im.uicommon.listener.OnLocalMessageOperateListener
import io.im.uicommon.model.UiSession
import io.im.uicommon.repo.ConversationRepo
import io.im.uicommon.route.IMRoute
import io.im.uicommon.route.RouterConstant
import java.util.Collections


/**
 * by DAD FZ
 * 2026/6/11
 * desc：
 **/
class ConversationViewModel(application: Application) : AndroidViewModel(application),
    MessageEventListener, OnLocalMessageOperateListener, ChatLifecycle {

    private val mSessionLiveData = MediatorLiveData<MutableList<UiSession>>()
    private val mPageEventLiveData = MediatorLiveData<PageEvent>()
    private val mSessions = mutableListOf<UiSession>()

    private val sessionObserver = Observer<MutableList<Session>> { newList ->
        //去除交集，添加到列表中
        val sidSet = mSessions.map { it.session.sid }.toHashSet()
        val appendList = newList.filter {
            sidSet.add(it.sid)
        }
        if (appendList.isNotEmpty()) {
            appendList.forEach {
                mSessions.add(mapUISession(it))
            }
            refreshAllMessage()
        }
    }

    init {
        IMCenter.getInstance().getOptions().addMessageEventListener(this)
        IMCenter.getInstance().getOptions().localMessageOperateListeners.add(this)
        ChatSDK.getDbManager().sessionDao().getSessionLiveData(IMCenter.getAccountId())
            .observeForever(sessionObserver)
    }

    private fun mapUISession(session: Session): UiSession {
        val uiSession = UiSession(session)
        return uiSession
    }

    //本地删除后的最新消息
    override fun onDeletedAfterLastMessage(
        sid: String,
        message: Message?
    ) {
        ChatExecutorHelper.getInstance().diskIO().execute {
            val sessionResult = ChatSDK.getDbManager().sessionDao().getSessionBySid(sid)
            sessionResult?.let { session ->
                if (message != null) {
                    session.updateTime = message.updateTime
                    session.lastMessage = message.toJson()
                } else {
                    session.updateTime = ServeTime.currentTimeMillis()
                    session.lastMessage = ""
                }
                ChatSDK.getDbManager().sessionDao().update(session)
                val uiSession = findUISessionById(sid)
                uiSession?.let {
                    uiSession.session = session
                    refreshSingleMessage(it)
                }
            }
        }
    }

    fun onViewClick(
        view: View,
        clickType: Int,
        position: Int,
        data: UiSession
    ) {
        when (val conversationType = ConversationType.setValue(data.session.type)) {
            ConversationType.TYPE_P2P -> {
                IMRoute.withKey(RouterConstant.PAGE_CHAT_P2P)
                    .withContext(view.context)
                    .withParam(RouterConstant.USER, data.user)
                    .withParam(RouterConstant.CONVERSATION_TYPE, conversationType.value)
                    .navigate()
            }

            else -> {}
        }

    }

    fun onViewLongClick(
        view: View,
        clickType: Int,
        position: Int,
        data: UiSession
    ): Boolean {
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
        ConversationRepo.createConversation(message, true, object : FetchCallback<Session> {
            override fun onError(errorCode: Int, errorMsg: String?) {

            }

            override fun onSuccess(data: Session?) {
                data?.let { session ->
                    var uiSession = findUISessionById(session.sid)
                    val isAdd = uiSession == null
                    if (isAdd) {
                        uiSession = mapUISession(session)
                    } else {
                        uiSession.session = session
                    }
                    if (isAdd) {
                        sendSessionEvent(uiSession)
                    } else {
                        refreshAllMessage()
                    }
                    executePageEvent(ScrollToTopEvent())
                }
            }
        })
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
        Collections.sort(mSessions, ConversationUtil.getConversationComparator())
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            mSessionLiveData.value = mSessions
        } else {
            mSessionLiveData.postValue(mSessions)
        }
    }

    fun executePageEvent(pageEvent: PageEvent) {
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            mPageEventLiveData.value = pageEvent
        } else {
            mPageEventLiveData.postValue(pageEvent)
        }
    }

    fun getSessionLiveData(): MediatorLiveData<MutableList<UiSession>> {
        return mSessionLiveData
    }

    fun getPageEventLiveData(): MediatorLiveData<PageEvent> {
        return mPageEventLiveData
    }

    override fun onDestroy() {
        ChatSDK.getDbManager().sessionDao().getSessionLiveData(IMCenter.getAccountId())
            .removeObserver(sessionObserver)
        IMCenter.getInstance().getOptions().localMessageOperateListeners.remove(this)
        super.onDestroy()
    }

    fun operateDelete(uiSession: UiSession) {
        ConversationRepo.deleteConversation(
            uiSession.session.sid,
            true,
            ConversationType.setValue(uiSession.session.type),
            object : FetchCallback<Void> {
                override fun onError(errorCode: Int, errorMsg: String?) {

                }

                override fun onSuccess(data: Void?) {
                    mSessions.remove(uiSession)
                    refreshAllMessage()
                }
            })
    }

    fun operateStickTop(uiSession: UiSession) {
        ConversationRepo.setStickTop(
            uiSession.session.sid,
            !uiSession.isTop,
            object : FetchCallback<Session> {
                override fun onError(errorCode: Int, errorMsg: String?) {

                }

                override fun onSuccess(data: Session?) {
                    data?.let { d ->
                        val uiSession = findUISessionById(uiSession.session.sid)
                        uiSession?.let {
                            it.session = d
                            refreshAllMessage()
                        }
                    }
                }
            })
    }
}