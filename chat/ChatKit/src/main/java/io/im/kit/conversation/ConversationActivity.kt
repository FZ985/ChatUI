package io.im.kit.conversation

import android.os.Bundle
import io.im.kit.R
import io.im.kit.databinding.KitActivityConversationBinding
import io.im.kit.utils.RouteUtil
import io.im.lib.base.ChatBaseActivity
import io.im.lib.base.ChatFragmentPageAdapter
import io.im.lib.model.UserInfo


/**
 *  author : JFZ
 *  date : 2024/1/26 10:46
 *  description :聊天界面
 */
class ConversationActivity : ChatBaseActivity() {

    private val binding: KitActivityConversationBinding by lazy {
        KitActivityConversationBinding.inflate(layoutInflater)
    }

    private val adapter: ChatFragmentPageAdapter by lazy {
        ChatFragmentPageAdapter(supportFragmentManager, listOf(ConversationFragment()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.pager.adapter = adapter
        val user = intent.getSerializableExtra(RouteUtil.User) as UserInfo
        binding.conversationToolbar.setTitleName(user.userName)
        binding.conversationToolbar.setLeftOnclick { onBackPressed() }
    }

    override fun getChatActivityTheme(): Int {
        return R.style.Kit_Conversation_Theme
    }

    override fun onBackPressed() {
        val item = adapter.getItem(binding.pager.currentItem)
        val pressed = item.onBackPressed()
        if (!pressed) {
            super.onBackPressed()
        }
    }
}