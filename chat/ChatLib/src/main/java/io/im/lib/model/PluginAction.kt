package io.im.lib.model

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes


/**
 * by DAD FZ
 * 2026/5/28
 * desc：通用插件action数据类型 与UI解绑 可用于自定义插件
 **/
class PluginAction<T> @JvmOverloads constructor(
    val action: String,
    // 标题
    var title: String,
    @field:DrawableRes @get:DrawableRes
    @param:DrawableRes
    var icon: Int,
    actionClickListener: OnClickListener<T>? = null,
    var bean: T,
    // 标题资源
    @field:StringRes @get:StringRes
    @param:StringRes
    var titleRes: Int = 0
) {

    //点击事件
    var actionClickListener: OnClickListener<T>?

    init {
        this.actionClickListener = actionClickListener
    }

    fun interface OnClickListener<T> {
        fun onClick(
            view: View?,
            bean: T?
        )
    }
}