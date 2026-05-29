package io.im.kit.forward

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import io.im.kit.IMTest
import io.im.kit.MessageOperate
import io.im.kit.R
import io.im.kit.databinding.ForwardActivitySelectorBinding
import io.im.kit.helper.livedata.ChatLiveData
import io.im.kit.model.ForwardSelectorBean
import io.im.kit.utils.RouteUtil
import io.im.kit.widget.FixedLinearLayoutManager
import io.im.kit.widget.adapter.BaseAdapter
import io.im.kit.widget.adapter.ViewHolder
import io.im.lib.base.ChatBaseActivity
import io.im.lib.callback.MessageCallback
import io.im.lib.helper.ChatMsgCache
import io.im.lib.model.ConversationType
import io.im.lib.model.Message
import io.im.lib.model.UserInfo


/**
 * by DAD FZ
 * 2026/5/29
 * desc：
 **/
class IForwardSelectorActivity : ChatBaseActivity<ForwardActivitySelectorBinding>() {

    private val adapter = ForwardSelectorAdapter()

    private val isMerge: Boolean by lazy {
        intent.getBooleanExtra(RouteUtil.IsMerge, true)
    }

    private val user: UserInfo by lazy {
        intent.getSerializableExtra(RouteUtil.User) as? UserInfo ?: UserInfo()
    }

    override fun onInitPage(savedInstanceState: Bundle?) {
        binding.toolbar.setTitleRes(R.string.forward_t1)
        binding.toolbar.setLeftOnclick { finish() }
        binding.recycler.layoutManager = FixedLinearLayoutManager(this)
        binding.recycler.adapter = adapter

        adapter.setDataCollection(mutableListOf(IMTest.toUser))

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
                        ConversationType.PRIVATE,
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