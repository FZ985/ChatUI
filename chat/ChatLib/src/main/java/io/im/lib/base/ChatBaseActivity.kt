package io.im.lib.base

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import io.im.lib.R
import io.im.lib.utils.ActivityFix
import io.im.lib.utils.language.ChatConfigurationManager


/**
 *  author : JFZ
 *  date : 2024/1/26 10:33
 *  description :
 */
open class ChatBaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        val context = ChatConfigurationManager.getInstance().getConfigurationContext(newBase)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityFix.fixAndroid8ActivityCrash(this)
        super.onCreate(savedInstanceState)
        setTheme(getChatActivityTheme())
    }


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

}