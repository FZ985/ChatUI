package io.im.kit.conversation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import io.im.kit.callback.ConversationUserCall
import io.im.kit.conversation.extension.ConversationBottomHelper
import io.im.kit.databinding.KitFragmentConversationBinding
import io.im.kit.model.UiMessage
import io.im.kit.widget.adapter.IViewProviderListener
import io.im.kit.widget.switchpanel.PanelSwitchHelper
import io.im.kit.widget.switchpanel.interfaces.listener.OnKeyboardStateListener
import io.im.lib.base.ChatBaseFragment
import io.im.lib.message.TextMessage
import io.im.lib.message.UnKnowMessage
import io.im.lib.model.ConversationType
import io.im.lib.model.Message
import io.im.lib.model.MessageContent
import io.im.lib.model.UserInfo
import io.im.lib.utils.JLog
import java.util.Random


/**
 *  author : JFZ
 *  date : 2024/1/26 11:46
 *  description :
 */
class ConversationFragment : ChatBaseFragment(), ConversationUserCall, OnRefreshListener,
    IViewProviderListener<UiMessage> {

    private val helper = ConversationBottomHelper()

    private lateinit var userInfo: UserInfo

    private var conversationType = ConversationType.PRIVATE

    private val adapter = ConversationListAdapter(this)

    var activitySoftInputMode = 0

    private val binding: KitFragmentConversationBinding by lazy {
        KitFragmentConversationBinding.inflate(layoutInflater)
    }

    private var mHelper: PanelSwitchHelper?=null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.refresh.setColorSchemeResources(io.im.lib.R.color.chat_theme)
//        binding.refresh.setOnRefreshListener(this)
//
//        val layoutManager = FixedLinearLayoutManager(context)
//        layoutManager.stackFromEnd = true
//        binding.recycler.layoutManager = layoutManager
//        val itemAnimator = DefaultItemAnimator()
//        itemAnimator.supportsChangeAnimations = false
//        binding.recycler.itemAnimator = itemAnimator
//        binding.recycler.adapter = adapter
//        val gd = GestureDetector(mActivity, object : GestureDetector.SimpleOnGestureListener() {
//            override fun onScroll(
//                e1: MotionEvent?,
//                e2: MotionEvent,
//                distanceX: Float,
//                distanceY: Float
//            ): Boolean {
//                closeExpand()
//                return super.onScroll(e1, e2, distanceX, distanceY)
//            }
//
//            override fun onSingleTapUp(e: MotionEvent): Boolean {
//                closeExpand()
//                return super.onSingleTapUp(e)
//            }
//        })
//        binding.recycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
//
//            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                return gd.onTouchEvent(e)
//            }
//
//            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
//            }
//
//            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//            }
//        })
//
//        userInfo = requireActivity().intent.getSerializableExtra(RouteUtil.User) as UserInfo
//        conversationType = ConversationType.setValue(
//            requireActivity().intent.getIntExtra(
//                RouteUtil.ConversationType,
//                ConversationType.PRIVATE.value
//            )
//        )
//        helper.bindUI(this, binding.inputPanel, binding.pluginPanel, this)
//
//        test()
    }

    private fun test() {
        val list = arrayListOf<UiMessage>().apply {
            add(UiMessage(getSendMsg(TextMessage.obtain("123"))))
            add(UiMessage(getSendMsg(TextMessage.obtain("123"))))
            add(UiMessage(getReceiveMsg(TextMessage.obtain("234"))))
            add(UiMessage(getReceiveMsg(TextMessage.obtain("234"))))
            add(UiMessage(getReceiveMsg(TextMessage.obtain("234"))))
            add(UiMessage(getReceiveMsg(TextMessage.obtain("234"))))
            add(UiMessage(getSendMsg(TextMessage.obtain("123123123123123")).setReadStatus(Message.ReadStatus.READ)))
            add(UiMessage(getReceiveMsg(TextMessage.obtain("223423423423423423434234234234234234234234234234234234234234234234234234234234234234234234"))))
            add(UiMessage(getReceiveMsg(TextMessage.obtain("234234234234234234234234234234234234234234234234234234"))))
            add(UiMessage(Message(UnKnowMessage())))
        }
        adapter.setDataCollection(list)
    }


    private fun getSendMsg(content: MessageContent): Message {
        val msg = Message(content)
        msg.messageDirection = Message.MessageDirection.SEND
        msg.messageTime =
            System.currentTimeMillis() - (Random().nextInt(100) + 10) * 60 * 1000
        return msg
    }

    private fun getReceiveMsg(content: MessageContent): Message {
        val msg = Message(content)
        msg.messageDirection = Message.MessageDirection.RECEIVE
        msg.messageTime =
            System.currentTimeMillis() - (Random().nextInt(100) + 10) * 60 * 1000
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
//        binding.refresh.postDelayed({
//            binding.refresh.isRefreshing = false
//        }, 500)
    }

    override fun onStart() {
        super.onStart()
//        JLog.e("useKeyboardHeightProvider:" + useKeyboardHeightProvider())
//        val activity = activity
//        if (activity != null) {
//            activitySoftInputMode = activity.window.attributes.softInputMode
//            if (useKeyboardHeightProvider()) {
//                resetSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
//            } else {
//                resetSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        helper.onResume()
        if (mHelper == null){
            mHelper = PanelSwitchHelper.Builder(this)
                .setWindowInsetsRootView(binding.root)
                .addKeyboardStateListener(object : OnKeyboardStateListener {
                    override fun onKeyboardChange(visible: Boolean, height: Int) {
                        JLog.e("====key:" + visible + "," + height)
                    }
                })
                .logTrack(true)
                .build(true)
        }
        binding.recycler.setPanelSwitchHelper(mHelper)
    }

    override fun onPause() {
        super.onPause()
        helper.onPause()
    }

    override fun onStop() {
        super.onStop()
        helper.onStop()
        resetSoftInputMode(activitySoftInputMode)
    }

    override fun onDestroyView() {
        helper.onDestroy()
        super.onDestroyView()
    }

    fun useKeyboardHeightProvider(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val activity = activity
            return activity != null && !activity.isInMultiWindowMode
        }
        return false
    }

    private fun resetSoftInputMode(mode: Int) {
        val activity = activity
        activity?.window?.setSoftInputMode(mode)
    }

}