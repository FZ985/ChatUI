package io.im.kit.utils

import android.view.View


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