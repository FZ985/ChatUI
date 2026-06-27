package io.im.uicommon.route


/**
 * by DAD FZ
 * 2026/6/27
 * desc：
 **/
interface ResultObserver<T> {

    /**
     * can call this method to inform user the result.
     *
     * @param result result of execution.
     */
    fun onResult(result: ResultInfo<T>)
}