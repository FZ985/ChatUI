package io.im.lib.base

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import io.im.lib.R
import io.im.lib.utils.ActivityFix
import io.im.lib.utils.language.ChatConfigurationManager


/**
 *  author : JFZ
 *  date : 2024/1/26 10:33
 *  description :
 */
abstract class ChatBaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected open val binding: VB by lazy(LazyThreadSafetyMode.NONE) {
        getBinding(layoutInflater)
    }

    override fun attachBaseContext(newBase: Context?) {
        val context = ChatConfigurationManager.getInstance().getConfigurationContext(newBase)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityFix.fixAndroid8ActivityCrash(this)
        super.onCreate(savedInstanceState)
        setTheme(getChatActivityTheme())
        setContentView(binding.root)
        if (compatEdgeToEdge()) {
            enableEdgeToEdge()
            ViewCompat.setOnApplyWindowInsetsListener(
                binding.root
            ) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }
        }
        onInitPage(savedInstanceState)
    }

    abstract fun onInitPage(savedInstanceState: Bundle?)

    private var imm: InputMethodManager? = null

    protected fun hideSoftKeyBoard() {
        val localView = currentFocus
        if (this.imm == null) {
            this.imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }
        if ((localView != null) && (this.imm != null)) {
            this.imm?.hideSoftInputFromWindow(localView.windowToken, 0)
        }
    }

    open fun getChatActivityTheme(): Int {
        return R.style.Chat_Theme_Base
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        if (res != null) {
            val config = res.configuration
            if (config != null && config.fontScale != 1.0f) {
                config.fontScale = 1.0f
                res.updateConfiguration(config, res.displayMetrics)
            }
        }
        return res
    }

    abstract fun getBinding(inflater: LayoutInflater): VB

    fun compatEdgeToEdge(): Boolean = true
}