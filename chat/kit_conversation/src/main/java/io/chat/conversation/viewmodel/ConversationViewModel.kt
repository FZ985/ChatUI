package io.chat.conversation.viewmodel

import android.app.Application
import android.os.Looper
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import io.chat.kit.ChatRoute
import io.im.core.model.Session
import io.im.core.utils.ChatToast
import io.im.uicommon.IMCenter
import io.im.uicommon.UserTest
import io.im.uicommon.event.ChatMessageEvent
import io.im.uicommon.listener.MessageEventListener


/**
 * by DAD FZ
 * 2026/6/11
 * desc：
 **/
class ConversationViewModel(application: Application) : AndroidViewModel(application),
    MessageEventListener {

    private val mSessionLiveData = MediatorLiveData<MutableList<Session>>()

    private val mSessions = mutableListOf<Session>()

    init {
        IMCenter.getInstance().getOptions().addMessageEventListener(this)

        //模拟数据
        val list = mutableListOf<Session>()
        val users = UserTest.randomUserList()
        users.forEachIndexed { index, user ->
            list.add(Session().apply {
                sid = user.id
                session = user.toJson()
                isTop = if (index < 3) 1 else 0
            })
        }
        mSessions.addAll(list)
        refreshAllMessage()
    }

    fun onViewClick(
        view: View,
        clickType: Int,
        position: Int,
        data: Session
    ) {
        ChatRoute.goPrivateChat(view.context, data.user)
    }

    fun onViewLongClick(
        view: View,
        clickType: Int,
        position: Int,
        data: Session
    ): Boolean {
        ChatToast.toast(view.context, "long click")
        return true
    }

    override fun onSendMediaMessage(event: ChatMessageEvent) {
        onSendMessage(event)
    }

    override fun onSendMessage(event: ChatMessageEvent) {

    }

    override fun onReceiveMessage(event: ChatMessageEvent) {

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

    private fun excListLiveData() {
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            mSessionLiveData.value = mSessions
        } else {
            mSessionLiveData.postValue(mSessions)
        }
    }

    fun getSessionLiveData(): MediatorLiveData<MutableList<Session>> {
        return mSessionLiveData
    }
}