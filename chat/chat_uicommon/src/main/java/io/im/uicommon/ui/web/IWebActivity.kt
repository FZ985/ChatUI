package io.im.uicommon.ui.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import io.im.uicommon.R
import io.im.uicommon.base.ChatBaseActivity
import io.im.uicommon.databinding.CommonChatActivityWebviewBinding


/**
 * by DAD FZ
 * 2026/5/23
 * desc：
 **/
class IWebActivity : ChatBaseActivity<CommonChatActivityWebviewBinding>() {

    private lateinit var fragment: IWebFragment

    override fun onInitPage(savedInstanceState: Bundle?) {
        val transaction = supportFragmentManager.beginTransaction()
        fragment = IWebFragment.instance(intent.getStringExtra("url") ?: "")
        transaction.add(R.id.main_content, fragment)
        transaction.show(fragment)
        transaction.commitAllowingStateLoss()
        binding.toolbar.setLeftOnclick {
            onBackPressed()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (fragment != null && fragment.onBack()) {
        } else {
            finish()
        }
    }

    companion object {

        @JvmStatic
        fun startWeb(fragment: Fragment, url: String) {
            fragment.startActivity(
                Intent(
                    fragment.activity,
                    IWebActivity::class.java
                ).putExtra("url", url)
            )
        }

        @JvmStatic
        fun startWeb(context: Context, url: String) {
            val intent = Intent(context, IWebActivity::class.java).putExtra("url", url)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun getBinding(inflater: LayoutInflater) = CommonChatActivityWebviewBinding.inflate(inflater)
}