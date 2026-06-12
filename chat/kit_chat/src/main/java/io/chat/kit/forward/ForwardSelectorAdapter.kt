package io.chat.kit.forward

import android.annotation.SuppressLint
import io.im.core.model.UserInfo
import io.im.uicommon.adapter.BaseAdapter


/**
 * by DAD FZ
 * 2026/5/29
 * desc：
 **/
class ForwardSelectorAdapter : BaseAdapter<UserInfo>() {

    init {
        mProviderManager.addProvider(ForwardSelectorProvider())
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun setDataCollection(data: List<UserInfo>) {
        super.setDataCollection(data)
        notifyDataSetChanged()
    }

}