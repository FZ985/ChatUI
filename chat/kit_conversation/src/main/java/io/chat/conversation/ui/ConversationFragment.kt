package io.chat.conversation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import io.chat.conversation.R
import io.chat.conversation.adapter.ConversationAdapter
import io.chat.conversation.databinding.ConFragmentConversationBinding
import io.im.core.model.Session
import io.im.uicommon.UserTest
import io.im.uicommon.adapter.ViewHolder
import io.im.uicommon.base.ChatBaseFragment
import io.im.uicommon.widgets.FixedLinearLayoutManager

/**
 * by DAD FZ
 * 2026/6/10
 * desc：
 **/
class ConversationFragment : ChatBaseFragment() {

    private val binding: ConFragmentConversationBinding by lazy {
        ConFragmentConversationBinding.inflate(layoutInflater)
    }

    private val adapter = ConversationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolBar.setTitleRes(R.string.conversation_title)
        binding.toolBar.hideLeft()
        binding.toolBar.showLine(true)
        binding.recycler.layoutManager = FixedLinearLayoutManager(mActivity)
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.supportsChangeAnimations = false
        binding.recycler.setItemAnimator(itemAnimator)
        binding.recycler.adapter = adapter

        adapter.setItemClickListener(object :
            io.im.uicommon.adapter.BaseAdapter.OnItemClickListener {
            override fun onItemClick(
                view: View,
                holder: ViewHolder,
                position: Int
            ) {
            }

            override fun onItemLongClick(
                view: View,
                holder: ViewHolder,
                position: Int
            ): Boolean {
                return false
            }
        })
        testData()
    }

    private fun testData() {
        val list = mutableListOf<Session>()
        val users = UserTest.randomUserList()
        users.forEach { user ->
            list.add(Session().apply {
                sid = user.id
                session = user.toJson()
            })
        }
        adapter.setDataCollection(list)
    }

}