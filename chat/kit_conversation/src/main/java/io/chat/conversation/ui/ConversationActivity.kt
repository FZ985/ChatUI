package io.chat.conversation.ui

import android.os.Bundle
import android.view.LayoutInflater
import io.chat.conversation.databinding.ConActivityConversationBinding
import io.im.uicommon.base.ChatBaseActivity
import io.im.uicommon.base.ChatBaseFragment
import io.im.uicommon.base.ChatFragmentPageAdapter


/**
 * by DAD FZ
 * 2026/6/10
 * desc：
 **/
class ConversationActivity : ChatBaseActivity<ConActivityConversationBinding>() {
    override fun onInitPage(savedInstanceState: Bundle?) {
        val fragments = mutableListOf<ChatBaseFragment>()
        fragments.add(ConversationFragment())
        binding.conVp.adapter = ChatFragmentPageAdapter(supportFragmentManager, fragments)
    }

    override fun getBinding(inflater: LayoutInflater) =
        ConActivityConversationBinding.inflate(inflater)
}