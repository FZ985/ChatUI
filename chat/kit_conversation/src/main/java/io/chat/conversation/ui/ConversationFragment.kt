package io.chat.conversation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import io.chat.conversation.R
import io.chat.conversation.adapter.ConversationAdapter
import io.chat.conversation.databinding.ConFragmentConversationBinding
import io.chat.conversation.viewmodel.ConversationViewModel
import io.im.core.model.Session
import io.im.core.utils.ChatNull
import io.im.uicommon.adapter.IViewProviderListener
import io.im.uicommon.base.ChatBaseFragment
import io.im.uicommon.widgets.FixedLinearLayoutManager

/**
 * by DAD FZ
 * 2026/6/10
 * desc：
 **/
class ConversationFragment : ChatBaseFragment(), IViewProviderListener<Session> {

    private val binding: ConFragmentConversationBinding by lazy {
        ConFragmentConversationBinding.inflate(layoutInflater)
    }

    private val adapter = ConversationAdapter(this)

    private val viewmodel: ConversationViewModel by lazy {
        ViewModelProvider(this)[ConversationViewModel::class.java]
    }

    private val mListObserver = Observer<MutableList<Session>> {
        adapter.setDataCollection(ChatNull.compatList(it))
    }

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

        viewmodel.getSessionLiveData().observeForever(mListObserver)
    }

    override fun onViewClick(
        view: View,
        clickType: Int,
        position: Int,
        data: Session
    ) {
        viewmodel.onViewClick(view, clickType, position, data)
    }

    override fun onViewLongClick(
        view: View,
        clickType: Int,
        position: Int,
        data: Session
    ): Boolean {
        return viewmodel.onViewLongClick(view, clickType, position, data)
    }

    override fun onDestroyView() {
        viewmodel.getSessionLiveData().removeObserver(mListObserver)
        super.onDestroyView()
    }

}