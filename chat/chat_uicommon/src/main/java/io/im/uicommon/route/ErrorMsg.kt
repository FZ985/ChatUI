package io.im.uicommon.route


/**
 * by DAD FZ
 * 2026/6/27
 * desc：
 **/
class ErrorMsg @JvmOverloads constructor(
    val code: Int, // code of error
    val message: String? = "", // message
    val exception: Throwable? = null // exception of result
) {
    override fun toString(): String {
        return "ErrorMsg(code=$code, message='$message', exception=$exception)"
    }
}