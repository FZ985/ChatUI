package io.chat.conversation.ui

import android.content.Intent
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
import io.chat.conversation.model.UiSession
import io.chat.conversation.viewmodel.ConversationViewModel
import io.chat.kit.event.PageEvent
import io.chat.kit.event.ScrollToTopEvent
import io.im.core.utils.ChatNull
import io.im.uicommon.adapter.IViewProviderListener
import io.im.uicommon.base.ChatBaseFragment
import io.im.uicommon.widgets.FixedLinearLayoutManager

/**
 * by DAD FZ
 * 2026/6/10
 * desc：
 **/
class ConversationFragment : ChatBaseFragment(), IViewProviderListener<UiSession> {

    private val binding: ConFragmentConversationBinding by lazy {
        ConFragmentConversationBinding.inflate(layoutInflater)
    }

    private val adapter = ConversationAdapter(this)

    private val viewmodel: ConversationViewModel by lazy {
        ViewModelProvider(this)[ConversationViewModel::class.java]
    }

    private val mListObserver = Observer<MutableList<UiSession>> {
        adapter.setDataCollection(ChatNull.compatList(it))
    }

    private val mPageObserver = Observer<PageEvent> { pageEvent ->
        if (isDetached) return@Observer
        if (pageEvent is ScrollToTopEvent) {
            binding.recycler.scrollToPosition(adapter.headersCount)
        }
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
        viewmodel.getPageEventLiveData().observeForever(mPageObserver)
    }

    override fun onViewClick(
        view: View,
        clickType: Int,
        position: Int,
        data: UiSession
    ) {
        viewmodel.onViewClick(view, clickType, position, data)
    }

    override fun onViewLongClick(
        view: View,
        clickType: Int,
        position: Int,
        data: UiSession
    ): Boolean {
        return viewmodel.onViewLongClick(view, clickType, position, data)
    }

    override fun onResume() {
        super.onResume()
        viewmodel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewmodel.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewmodel.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewmodel.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        viewmodel.onDestroy()
        viewmodel.getSessionLiveData().removeObserver(mListObserver)
        viewmodel.getPageEventLiveData().removeObserver(mPageObserver)
        super.onDestroyView()
    }

}