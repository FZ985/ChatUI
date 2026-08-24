package io.ai.chat.ui

import android.os.Bundle
import android.view.LayoutInflater
import io.ai.chat.databinding.AiActivityChatBinding
import io.im.uicommon.base.ChatBaseActivity
import io.im.uicommon.base.ChatBaseFragment
import io.im.uicommon.base.ChatFragmentPageAdapter
import java.io.Serializable


/**
 * by DAD FZ
 * 2026/8/20
 * desc：
 **/
class AIChatActivity : ChatBaseActivity<AiActivityChatBinding>() {

    private lateinit var chatFragment: AIChatFragment

    override fun onInitPage(savedInstanceState: Bundle?) {
        val fragments = mutableListOf<ChatBaseFragment>()
        chatFragment = AIChatFragment()
        fragments.add(chatFragment)
        binding.aiPage.setAdapter(ChatFragmentPageAdapter(supportFragmentManager, fragments))
    }

    override fun onBackPressed() {
        val pressed = chatFragment.onBackPressed()
        if (!pressed) {
            super.onBackPressed()
        }
    }

    override fun getBinding(inflater: LayoutInflater) = AiActivityChatBinding.inflate(inflater)
}

data class Test(val name: StringBuilder? = StringBuilder(), val age: Int) : Serializable