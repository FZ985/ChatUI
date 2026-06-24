package io.chat.kit.forward

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import io.chat.kit.ChatRoute
import io.chat.kit.R
import io.chat.kit.databinding.ForwardActivitySelectorBinding
import io.chat.kit.helper.livedata.ChatLiveData
import io.chat.kit.model.ForwardSelectorBean
import io.im.core.listener.MessageCallback
import io.im.core.model.ConversationType
import io.im.core.model.Message
import io.im.core.model.UserInfo
import io.im.uicommon.MessageOperate
import io.im.uicommon.UserTest
import io.im.uicommon.adapter.BaseAdapter
import io.im.uicommon.adapter.ViewHolder
import io.im.uicommon.base.ChatBaseActivity
import io.im.uicommon.helper.ChatMsgCache
import io.im.uicommon.widgets.FixedLinearLayoutManager


/**
 * by DAD FZ
 * 2026/5/29
 * desc：
 **/
class IForwardSelectorActivity : ChatBaseActivity<ForwardActivitySelectorBinding>() {

    private val adapter = ForwardSelectorAdapter()

    private val isMerge: Boolean by lazy {
        intent.getBooleanExtra(ChatRoute.IsMerge, true)
    }

    private val user: UserInfo by lazy {
        intent.getSerializableExtra(ChatRoute.User) as? UserInfo ?: UserInfo()
    }

    override fun onInitPage(savedInstanceState: Bundle?) {
        binding.toolbar.setTitleRes(R.string.forward_t1)
        binding.toolbar.setLeftOnclick { finish() }
        binding.recycler.layoutManager = FixedLinearLayoutManager(this)
        binding.recycler.adapter = adapter

        adapter.setDataCollection(UserTest.randomUserList(50))

        adapter.setItemClickListener(object : BaseAdapter.OnItemClickListener {
            override fun onItemClick(
                view: View,
                holder: ViewHolder,
                position: Int
            ) {
                val sendUser = adapter.getItem(position)
                val userList = mutableListOf(sendUser)
                val messages = ChatMsgCache.getMessageList()
                if (isMerge) {
                    //合并发送
                    MessageOperate.sendMergeForwardMessage(
                        ConversationType.TYPE_P2P,
                        user,
                        messages,
                        userList,
                        object : MessageCallback<Message> {
                            override fun onSuccess(message: Message) {
                                if (isFinishing) return
                                finish()
                            }

                            override fun onError(
                                message: Message,
                                errorCode: Int
                            ) {
                                if (isFinishing) return
                                finish()
                            }
                        }
                    )
                } else {
                    //逐条发送
                    MessageOperate.sendForwardMessage(
                        messages,
                        userList
                    ) { successList, errorList ->
                        ChatLiveData.getInstance().forwardLive.value =
                            ForwardSelectorBean(userList, "")
                        ChatMsgCache.clear()
                        finish()
                    }
                }
            }

            override fun onItemLongClick(
                view: View,
                holder: ViewHolder,
                position: Int
            ): Boolean = false
        })
    }

    override fun isSafeTop() = true

    override fun getBinding(inflater: LayoutInflater) =
        ForwardActivitySelectorBinding.inflate(inflater)
}