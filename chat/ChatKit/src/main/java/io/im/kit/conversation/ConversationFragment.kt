package io.im.kit.conversation

import android.content.Context
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import io.im.kit.callback.ConversationUserCall
import io.im.kit.conversation.extension.ConversationBottomHelper
import io.im.kit.databinding.KitFragmentConversationBinding
import io.im.kit.model.UiMessage
import io.im.kit.utils.RouteUtil
import io.im.kit.widget.FixedLinearLayoutManager
import io.im.kit.widget.adapter.IViewProviderListener
import io.im.lib.base.ChatBaseFragment
import io.im.lib.message.TextMessage
import io.im.lib.message.UnKnowMessage
import io.im.lib.model.ConversationType
import io.im.lib.model.Message
import io.im.lib.model.MessageContent
import io.im.lib.model.UserInfo


/**
 *  author : JFZ
 *  date : 2024/1/26 11:46
 *  description :
 */
class ConversationFragment : ChatBaseFragment(), ConversationUserCall, OnRefreshListener,
    IViewProviderListener<UiMessage> {

    private lateinit var mActivity: AppCompatActivity

    private val helper = ConversationBottomHelper()

    private lateinit var userInfo: UserInfo

    private var conversationType = ConversationType.PRIVATE

    private val adapter = ConversationListAdapter(this)

    private val binding: KitFragmentConversationBinding by lazy {
        KitFragmentConversationBinding.inflate(layoutInflater)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.refresh.setColorSchemeResources(io.im.lib.R.color.chat_theme)
        binding.refresh.setOnRefreshListener(this)

        val layoutManager = FixedLinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        binding.recycler.layoutManager = layoutManager
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.supportsChangeAnimations = false
        binding.recycler.itemAnimator = itemAnimator
        binding.recycler.adapter = adapter
        val gd = GestureDetector(mActivity, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                closeExpand()
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
        })
        binding.recycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return gd.onTouchEvent(e)
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        })

        userInfo = requireActivity().intent.getSerializableExtra(RouteUtil.User) as UserInfo
        conversationType = ConversationType.setValue(
            requireActivity().intent.getIntExtra(
                RouteUtil.ConversationType,
                ConversationType.PRIVATE.value
            )
        )
        helper.bindUI(this, binding.inputPanel, this)

        test()
    }

    private fun test() {
        val list = arrayListOf<UiMessage>().apply {
            add(UiMessage(getSendMsg(TextMessage.obtain("123"))))
            add(UiMessage(getSendMsg(TextMessage.obtain("123")).setReadStatus(Message.ReadStatus.READ)))
            add(UiMessage(getReceiveMsg(TextMessage.obtain("234"))))
        }
        adapter.setDataCollection(list)
    }


    private fun getSendMsg(content: MessageContent): Message {
        val msg = Message(content)
        msg.messageDirection = Message.MessageDirection.SEND
        msg.messageTime = System.currentTimeMillis() - 5 * 60 * 1000
        return msg
    }

    private fun getReceiveMsg(content: MessageContent): Message {
        val msg = Message(content)
        msg.messageDirection = Message.MessageDirection.RECEIVE
        msg.messageTime = System.currentTimeMillis() - 5 * 60 * 1000
        return msg
    }

    override fun onViewClick(view: View, clickType: Int, data: UiMessage) {

    }

    override fun onViewLongClick(view: View, clickType: Int, data: UiMessage): Boolean {
        return false
    }

    private fun closeExpand() {
//        mChatExtensionViewModel.collapseExtensionBoard()
    }

    override fun updateUser(user: UserInfo) {
        this.userInfo = user
        helper.updateConversationUserCall(this)
    }

    override fun getUser(): UserInfo {
        return userInfo
    }

    fun getConversationType(): ConversationType {
        return conversationType
    }

    override fun onRefresh() {
        binding.refresh.postDelayed({
            binding.refresh.isRefreshing = false
        }, 500)
    }

    override fun onResume() {
        super.onResume()
        helper.onResume()
    }

    override fun onPause() {
        super.onPause()
        helper.onPause()
    }

    override fun onStop() {
        super.onStop()
        helper.onStop()
    }

    override fun onDestroyView() {
        helper.onDestroy()
        super.onDestroyView()
    }
}