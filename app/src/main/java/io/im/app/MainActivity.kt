package io.im.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.chat.conversation.ConversationRoute
import io.chat.kit.ChatRoute
import io.im.app.databinding.ActivityMainBinding
import io.im.uicommon.UserTest

class MainActivity : AppCompatActivity() {


    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root
        ) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.goChat.setOnClickListener {
            ChatRoute.goPrivateChat(this, UserTest.randomUser())
        }

        binding.conversation.setOnClickListener {
            ConversationRoute.goConversation(this)
        }

        binding.refresh.setOnClickListener {
            ImDebug.switchLoginUser()
            refreshLoginUI()
        }

        refreshLoginUI()
    }

    @SuppressLint("SetTextI18n")
    private fun refreshLoginUI() {
        val user = ImDebug.getLoginUser()
        Glide.with(this)
            .load(user.avatar)
            .error(io.im.uicommon.R.mipmap.kit_ic_default_avatar)
            .transition(DrawableTransitionOptions.withCrossFade())
            .transform(CenterCrop(), CircleCrop())
            .into(binding.loginHead)

        binding.loginName.text = user.name
        binding.loginId.text = "ID:" + "${user.id}"
    }

    private fun jumpToAppStoreDetailUpdate() {
        val intent = Intent(Intent.ACTION_VIEW)
        val packageName = "com.yilian.qkbigmarket"
        // th_update_delay=1代表使用默认接口方案（预约闲时更新），如果移除该参数代表使
        //用备选接口方案（立即更新）
        val url = "market://details?id=" + packageName + "&th_name=self_update&th_update_delay=1"
        val uri = url.toUri()
        intent.setData(uri)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}