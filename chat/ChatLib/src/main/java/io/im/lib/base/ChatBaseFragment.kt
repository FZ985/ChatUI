package io.im.lib.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


/**
 *  author : JFZ
 *  date : 2024/1/26 11:44
 *  description :
 */
open class ChatBaseFragment : Fragment() {


    protected lateinit var mActivity: AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    fun onBackPressed(): Boolean {
        return false
    }
}