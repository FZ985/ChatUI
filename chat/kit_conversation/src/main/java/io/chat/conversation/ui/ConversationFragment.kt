package io.chat.conversation.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import io.chat.conversation.R
import io.chat.conversation.adapter.ConversationAdapter
import io.chat.conversation.databinding.ConFragmentConversationBinding
import io.chat.conversation.databinding.ConHeadTopNetworkBinding
import io.chat.conversation.viewmodel.ConversationViewModel
import io.im.uicommon.event.PageEvent
import io.im.uicommon.event.ScrollToTopEvent
import io.im.core.core.socket.ErrorResult
import io.im.core.core.socket.SocketCode
import io.im.core.listener.OnConnectListener
import io.im.core.utils.ChatNull
import io.im.uicommon.IMCenter
import io.im.uicommon.adapter.IViewProviderListener
import io.im.uicommon.base.ChatBaseFragment
import io.im.uicommon.helper.IMAlertHelper
import io.im.uicommon.model.UiSession
import io.im.uicommon.widgets.FixedLinearLayoutManager
import io.im.uicommon.widgets.swiperecycler.SwipeMenuItem

/**
 * by DAD FZ
 * 2026/6/10
 * desc：
 **/
class ConversationFragment : ChatBaseFragment(), IViewProviderListener<UiSession>,
    OnConnectListener {

    private val binding: ConFragmentConversationBinding by lazy {
        ConFragmentConversationBinding.inflate(layoutInflater)
    }

    private val head: ConHeadTopNetworkBinding by lazy {
        ConHeadTopNetworkBinding.inflate(layoutInflater).apply {
            root.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private val adapter = ConversationAdapter(this)

    private val viewmodel: ConversationViewModel by lazy {
        ViewModelProvider(this)[ConversationViewModel::class.java]
    }

    private val mListObserver = Observer<MutableList<UiSession>> {
        adapter.setDataCollection(binding.recycler, ChatNull.compatList(it))
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
        binding.toolBar.showLine(false)
        binding.recycler.layoutManager = FixedLinearLayoutManager(mActivity)
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.supportsChangeAnimations = false
        binding.recycler.setItemAnimator(itemAnimator)
//        binding.recycler.setItemAnimator(null)
        binding.recycler.setSwipeMenuCreator { _, rightMenu, position ->
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val item = adapter.getItem(position)
            val isTop = item.isTop
            val dp100 = resources.getDimensionPixelSize(io.im.core.R.dimen.chat_dp100)
            val dp80 = resources.getDimensionPixelSize(io.im.core.R.dimen.chat_dp80)

            val topItem = SwipeMenuItem(mActivity)
                .setBackground(io.im.uicommon.R.color.color_F3963B)
                .setText(if (isTop) io.im.uicommon.R.string.conversation_stick_top_cancel else io.im.uicommon.R.string.conversation_stick_top)
                .setWidth(if (isTop) dp100 else dp80)
                .setTextColor(Color.WHITE)
                .setHeight(height)
            rightMenu.addMenuItem(topItem)

            val deleteItem = SwipeMenuItem(mActivity)
                .setBackground(io.im.uicommon.R.color.color_FC3C30)
                .setText(io.im.uicommon.R.string.im_alert_delete)
                .setTextColor(Color.WHITE)
                .setWidth(dp80)
                .setHeight(height)
            rightMenu.addMenuItem(deleteItem)

        }
        binding.recycler.setOnItemMenuClickListener { menu, position ->
            val item = adapter.getItem(position)
            when (menu.position) {
                0 -> {
                    viewmodel.operateStickTop(item)
                }

                1 -> {
                    IMAlertHelper.with(mActivity)
                        .message(R.string.conversation_tip_delete)
                        .messagePadding(Rect(0, 30, 0, 30))
                        .confirm(io.im.uicommon.R.string.im_alert_known) {
                            it.dismiss()
                            viewmodel.operateDelete(item)
                        }.show()
                }
            }
            menu.closeMenu()
        }

        binding.recycler.adapter = adapter
        binding.recycler.addHeaderView(head.root)

        viewmodel.getSessionLiveData().observeForever(mListObserver)
        viewmodel.getPageEventLiveData().observeForever(mPageObserver)
        IMCenter.getInstance().options.connectService.addConnectListener(this)
    }

    override fun onConnected(result: ErrorResult) {
        head.conversationTip.isVisible =
            !(result.code == SocketCode.success || result.code == SocketCode.NETWORK_SUCCESS)
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
        IMCenter.getInstance().options.connectService.removeConnectListener(this)
        super.onDestroyView()
    }

}