package io.im.uicommon.route

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import io.im.core.utils.JLog
import java.io.Serializable
import java.util.PriorityQueue


/**
 * by DAD FZ
 * 2026/6/27
 * desc：
 **/
object IMRoute {

    private const val TAG = "IMRoute"

    /**
     * default size is 8 of interceptor
     */
    private const val DEFAULT_INTERCEPTOR_SIZE = 8

    /**
     * The table of router
     */
    private val routerMap = HashMap<RouterKey, RouterValue>()

    /**
     * The table of interceptor
     */
    private val interceptorQueue = PriorityQueue<Interceptor>(
        DEFAULT_INTERCEPTOR_SIZE
    ) { o1, o2 -> o2.priority() - o1.priority() }

    @JvmStatic
    fun registerRouter(path: String, clazz: Class<out Activity>) =
        registerRouter(path.toRouteKey(), clazz)

    @JvmStatic
    fun registerRouter(key: RouterKey, clazz: Class<out Activity>) =
        registerRouter(key, RouterValue(clazz, ActivityNavigator))

    @JvmStatic
    fun registerRouter(path: String, routerValue: RouterValue) =
        registerRouter(path.toRouteKey(), routerValue)

    /**
     * register router by key-value
     */
    @JvmStatic
    fun registerRouter(key: RouterKey, routerValue: RouterValue) = apply {
        if (routerMap.containsKey(key)) {
            // log to warning
            JLog.w(TAG, "This RouterKey$key had been registered for value${routerMap[key]}.")
        }
        routerMap[key] = routerValue
    }

    /**
     * register interceptor
     */
    @JvmStatic
    fun registerInterceptor(interceptor: Interceptor) = apply {
        if (interceptorQueue.contains(interceptor)) {
            // log to warn this interceptor had been registered.
            JLog.w(TAG, "This interceptor$interceptor had been registered.")
        }
        interceptorQueue.offer(interceptor)
    }

    @JvmStatic
    fun withKey(key: RouterKey): Navigation = key.run {
        Navigation(this, routerMap[this])
    }

    @JvmStatic
    fun withKey(path: String): Navigation = withKey(path.toRouteKey())

    /**
     * clear all router and interceptor
     */
    @JvmStatic
    fun clearAll() {
        routerMap.clear()
        interceptorQueue.clear()
    }

    /**
     * The navigation
     */
    class Navigation(
        private val routerKey: RouterKey,
        private val routerValue: RouterValue? = null
    ) {
        private val params = HashMap<String, Any?>()

        fun withParam(paramName: String, value: Any?) = apply {
            params[paramName] = value
        }

        fun withRequestCode(code: Int) = apply {
            params[ActivityNavigator.PARAM_REQUEST_CODE] = code
        }

        fun withContext(context: Context) = apply {
            params[ActivityNavigator.PARAM_CONTEXT] = context
        }

        fun withFragment(fragment: Fragment) = apply {
            params[ActivityNavigator.PARAM_FRAGMENT] = fragment
        }

        fun withBundleOptions(options: Bundle) = apply {
            params[ActivityNavigator.PARAM_OPTIONS] = options
        }

        fun withBundleParam(bundle: Bundle) = apply {
            params[ActivityNavigator.PARAM_BUNDLE] = bundle
        }

        fun withIntentParam(intent: Intent) = apply {
            params[ActivityNavigator.PARAM_INTENT] = intent
        }

        @JvmOverloads
        fun navigate(observer: ResultObserver<Any?>? = null): Boolean {
            // navigator was called to do some actions.
            return canHandleByNavigator(observer) && routerValue!!.navigator.navigate(
                routerValue.value,
                params,
                observer
            )
        }

        @JvmOverloads
        fun navigate(
            launcher: ActivityResultLauncher<Intent>,
            optionsCompat: ActivityOptionsCompat? = null,
            observer: ResultObserver<Any?>? = null
        ): Boolean {
            return canHandleByNavigator(observer) && (routerValue!!.navigator as ActivityNavigator).navigate(
                routerValue.value,
                params,
                observer,
                launcher,
                optionsCompat
            )
        }

        private fun canHandleByNavigator(observer: ResultObserver<Any?>?): Boolean {
            routerValue ?: run {
                // log no route value for key
                JLog.e(TAG, "Can't find value with key$routerKey.")
                return false
            }
            var handle = false
            // handle global interceptor
            if (routerValue.enableGlobalInterceptor) {
                for (interceptor in interceptorQueue) {
                    handle = interceptor.handle(routerKey, params, observer)
                    if (handle) {
                        break
                    }
                }
            }

            if (handle) {
                // log handle by interceptor
                JLog.d(
                    TAG,
                    "The value$routerValue with key$routerKey had been handled by interceptor."
                )
                return false
            }
            // handle ths self interceptor
            if (routerValue.interceptor?.handle(routerKey, params, observer) == true) {
                // log handle by self interceptor
                JLog.d(
                    TAG,
                    "The value$routerValue with key$routerKey had been handled by self interceptor."
                )
                return false
            }
            return true
        }
    }

    interface Navigator {
        /**
         * User can use router's value to do some actions, the false is
         */
        fun navigate(
            value: Any,
            params: MutableMap<String, Any?>,
            observer: ResultObserver<Any?>?
        ): Boolean
    }

    /**
     * Defined interceptor
     */
    abstract class Interceptor {
        /**
         * The value is bigger, the priority is higher. The interceptor can be called more early.
         */
        open fun priority(): Int = 0

        /**
         * The user can intercept router action by this method returning true.
         */
        abstract fun handle(
            key: RouterKey,
            param: MutableMap<String, Any?>,
            observer: ResultObserver<Any?>?
        ): Boolean
    }

    /**
     * Defined navigator of activity
     */
    object ActivityNavigator : Navigator {
        /**
         * key of request code
         */
        const val PARAM_REQUEST_CODE = "param_request_code"

        /**
         * key of context
         */
        const val PARAM_CONTEXT = "param_context"

        /**
         * key of fragment
         */
        const val PARAM_FRAGMENT = "param_fragment"

        /**
         * key of intent
         */
        const val PARAM_INTENT = "param_intent"

        /**
         * key of bundle
         */
        const val PARAM_BUNDLE = "param_bundle"

        /**
         * key of options
         */
        const val PARAM_OPTIONS = "param_options"

        override fun navigate(
            value: Any,
            params: MutableMap<String, Any?>,
            observer: ResultObserver<Any?>?
        ): Boolean {
            return navigate(value, params, observer, null, null)
        }

        fun navigate(
            value: Any,
            params: MutableMap<String, Any?>,
            observer: ResultObserver<Any?>?,
            launcher: ActivityResultLauncher<Intent>?,
            optionsCompat: ActivityOptionsCompat? = null
        ): Boolean {
            val context =
                params.remove(PARAM_CONTEXT) as? Context
                    ?: throw IllegalArgumentException("Launch context is null.")
            val options = params.remove(PARAM_OPTIONS) as? Bundle
            val fragment = params.remove(PARAM_FRAGMENT) as? Fragment
            val requestCode = params.remove(PARAM_REQUEST_CODE) as? Int
            val bundle = params.remove(PARAM_BUNDLE) as? Bundle
            val intentParam = params.remove(PARAM_INTENT) as? Intent

            val intent = Intent().apply {
                when (value) {
                    is Class<*> -> setClass(context, value)
                    else -> throw IllegalArgumentException(
                        "The router value is only supported type of Class< ? extend Activity>."
                    )
                }
                params.forEach {
                    putExtra(this, it.key, it.value)
                }
                bundle?.run {
                    putExtras(this)
                }
                intentParam?.run {
                    putExtras(this)
                }
            }

            return launcher?.run {
                launch(intent, optionsCompat)
                true
            } ?: run {
                startActivity(context, fragment, intent, requestCode, options)
            }
        }

        private fun putExtra(intent: Intent, key: String, value: Any?) {
            intent.run {
                when (value) {
                    is Int -> putExtra(key, value)
                    is Byte -> putExtra(key, value)
                    is Char -> putExtra(key, value)
                    is Long -> putExtra(key, value)
                    is Float -> putExtra(key, value)
                    is Short -> putExtra(key, value)
                    is Double -> putExtra(key, value)
                    is Boolean -> putExtra(key, value)
                    is Bundle -> putExtra(key, value)
                    is String -> putExtra(key, value)
                    is IntArray -> putExtra(key, value)
                    is ByteArray -> putExtra(key, value)
                    is CharArray -> putExtra(key, value)
                    is LongArray -> putExtra(key, value)
                    is FloatArray -> putExtra(key, value)
                    is Parcelable -> putExtra(key, value)
                    is ShortArray -> putExtra(key, value)
                    is DoubleArray -> putExtra(key, value)
                    is BooleanArray -> putExtra(key, value)
                    is CharSequence -> putExtra(key, value)
                    is Serializable -> putExtra(key, value)
                    else -> {
                        // log this value was missed to put.
                    }
                }
            }
        }

        private fun startActivity(
            context: Context,
            fragment: Fragment?,
            intent: Intent,
            requestCode: Int?,
            options: Bundle?
        ): Boolean = if (fragment != null) {
            startActivity(fragment, intent, requestCode, options)
        } else {
            startActivity(context, intent, requestCode, options)
        }

        private fun startActivity(
            context: Context,
            intent: Intent,
            requestCode: Int?,
            options: Bundle?
        ): Boolean {
            if (requestCode != null && requestCode >= 0 && context is Activity) {
                ActivityCompat.startActivityForResult(context, intent, requestCode, options)
            } else {
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                ActivityCompat.startActivity(context, intent, options)
            }
            return true
        }

        private fun startActivity(
            fragment: Fragment,
            intent: Intent,
            requestCode: Int?,
            options: Bundle?
        ): Boolean {
            return if (requestCode != null && requestCode >= 0) {
                fragment.activity?.run {
                    ActivityCompat.startActivityForResult(this, intent, requestCode, options)
                    true
                } ?: run {
                    false
                }
            } else {
                fragment.startActivity(intent, options)
                true
            }
        }
    }

    /**
     * The index of router.
     */
    class RouterKey(val path: String, val group: String? = null) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as RouterKey

            if (path != other.path) return false
            if (group != other.group) return false

            return true
        }

        override fun hashCode(): Int {
            var result = path.hashCode()
            result = 31 * result + group.hashCode()
            return result
        }

        override fun toString(): String {
            return "RouterKey(path='$path', group=$group)"
        }
    }

    /**
     * The value of router
     */
    class RouterValue @JvmOverloads constructor(
        val value: Any,
        val navigator: Navigator,
        val enableGlobalInterceptor: Boolean = true,
        val interceptor: Interceptor? = null
    ) {
        override fun toString(): String {
            return "RouterValue(value=$value, navigator=$navigator, enableGlobalInterceptor=$enableGlobalInterceptor, interceptor=$interceptor)"
        }
    }

    /**
     * Inner extension, user can use path to convert to index of router with optional group param.
     */
    private fun String.toRouteKey(group: String? = null): RouterKey = RouterKey(this, group)
}