package io.im.uicommon.route


/**
 * by DAD FZ
 * 2026/6/27
 * desc：
 **/
class ResultInfo<T> @JvmOverloads constructor(
    val value: T? = null, // result of value
    val success: Boolean = true, // result of execution, true is successful otherwise failing.
    val msg: ErrorMsg? = null // error message
) {
    override fun toString(): String {
        return "ResultInfo(value=$value, success=$success, msg=$msg)"
    }
}