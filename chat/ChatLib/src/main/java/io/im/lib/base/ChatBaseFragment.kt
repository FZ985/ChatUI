package io.im.lib.base

import androidx.fragment.app.Fragment


/**
 *  author : JFZ
 *  date : 2024/1/26 11:44
 *  description :
 */
open class ChatBaseFragment : Fragment() {


    fun onBackPressed(): Boolean {
        return false
    }
}