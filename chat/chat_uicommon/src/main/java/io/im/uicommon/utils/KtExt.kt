package io.im.uicommon.utils

import android.app.Activity
import android.content.ContextWrapper
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout


/**
 *  author : JFZ
 *  date : 2024/2/19 11:13
 *  description :
 */
fun View.absY(): Float {
    val location = IntArray(2)
    getLocationOnScreen(location)
    return (location[1].toFloat() - height.toFloat())
}

val Number.dp
    get() = (this.toFloat() * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun View.findActivity(): Activity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun View.decorViewOrRootView(): View? {
    findActivity()?.let {
        return it.window.decorView.findViewById<FrameLayout>(android.R.id.content)
    }
    return rootView
}