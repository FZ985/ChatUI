package io.im.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.im.app.databinding.ActivityMainBinding
import io.im.kit.utils.RouteUtil
import io.im.lib.model.UserInfo

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.goChat.setOnClickListener {
            RouteUtil.goPrivateChat(this, UserInfo("123", "哈哈哈", ""))
        }

    }
}