package io.chat.kit.forward

import android.annotation.SuppressLint
import io.im.core.model.User
import io.im.uicommon.adapter.BaseAdapter


/**
 * by DAD FZ
 * 2026/5/29
 * desc：
 **/
class ForwardSelectorAdapter : BaseAdapter<User>() {

    init {
        mProviderManager.addProvider(ForwardSelectorProvider())
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun setDataCollection(data: List<User>) {
        super.setDataCollection(data)
        notifyDataSetChanged()
    }

}