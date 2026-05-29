package io.im.kit.forward

import io.im.kit.widget.adapter.BaseAdapter
import io.im.lib.model.UserInfo


/**
 * by DAD FZ
 * 2026/5/29
 * desc：
 **/
class ForwardSelectorAdapter : BaseAdapter<UserInfo>() {

    init {
        mProviderManager.addProvider(ForwardSelectorProvider())
    }


    override fun setDataCollection(data: List<UserInfo>) {
        super.setDataCollection(data)
        notifyDataSetChanged()
    }

}