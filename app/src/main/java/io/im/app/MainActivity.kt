package io.im.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.im.app.databinding.ActivityMainBinding
import io.im.kit.IMTest
import io.im.kit.ui.popmenu.ChatPopMenu
import io.im.kit.utils.RouteUtil

class MainActivity : AppCompatActivity() {

    val popMenu = ChatPopMenu()

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
            RouteUtil.goPrivateChat(this, IMTest.toUser)
        }

    }
}