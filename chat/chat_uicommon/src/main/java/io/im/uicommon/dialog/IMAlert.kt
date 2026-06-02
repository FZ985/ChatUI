package io.im.uicommon.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.GravityInt
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.im.core.listener.ChatFun
import io.im.uicommon.databinding.CommonChatAlertBinding
import io.im.uicommon.helper.OptionsHelper
import io.im.uicommon.utils.dp


/**
 * by DAD FZ
 * 2026/6/1
 * desc：
 **/
class IMAlert : Dialog {

    private var title: CharSequence = ""
    private var message: CharSequence = ""
    private var cancel: CharSequence = ""
    private var confirm: CharSequence = ""

    private var titleGravity = Gravity.CENTER

    private var messageGravity = Gravity.CENTER

    private var cancelCall: ChatFun.Fun1<IMAlert>? = null
    private var confirmCall: ChatFun.Fun1<IMAlert>? = null

    private var messagePadding: Rect = Rect(0, 15, 0, 30)

    private var messageTextSize = 16

    private val binding: CommonChatAlertBinding by lazy {
        CommonChatAlertBinding.inflate(layoutInflater)
    }

    constructor(context: Context) : this(context, io.im.core.R.style.chat_core_dialog_custom)

    constructor(context: Context, themeRes: Int) : super(context, themeRes) {
        if (context is AppCompatActivity) {
            context.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    try {
                        if (isShowing) {
                            Log.e("MaterialDialog", "自动销毁dialog")
                            dismiss()
                        }
                    } catch (e: Exception) {
                        //to do
                    }
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.let {
            val lp = it.attributes
            lp.gravity = Gravity.CENTER
            lp.width = compatWidth(context.resources.displayMetrics.widthPixels, 0.76f)
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            it.attributes = lp
        }
        initView()
    }

    private fun initView() {
        if (title.isNotEmpty()) {
            binding.tipTitle.isVisible = true
            binding.tipTitle.text = title
            binding.tipTitle.gravity = titleGravity
            OptionsHelper.updateTextSize(binding.tipTitle, 18)
        }

        if (message.isNotEmpty()) {
            binding.tipMessage.isVisible = true
            binding.tipMessage.text = message
            binding.tipMessage.gravity = messageGravity
            OptionsHelper.updateTextSize(binding.tipMessage, messageTextSize)
            binding.tipMessage.setPadding(
                messagePadding.left.dp,
                messagePadding.top.dp,
                messagePadding.right.dp,
                messagePadding.bottom.dp
            )
        }

        binding.tipLineHor.isVisible = cancel.isNotEmpty() || confirm.isNotEmpty()
        binding.tipLineVer.isVisible = cancel.isNotEmpty() && confirm.isNotEmpty()

        if (cancel.isNotEmpty()) {
            binding.cancel.isVisible = true
            binding.cancel.text = cancel
            OptionsHelper.updateTextSize(binding.cancel, 16)
            binding.cancel.setOnClickListener {
                cancelCall?.apply(this) ?: run {
                    dismiss()
                }
            }
        }

        if (confirm.isNotEmpty()) {
            binding.confirm.isVisible = true
            binding.confirm.text = confirm
            OptionsHelper.updateTextSize(binding.cancel, 16)
            binding.confirm.setOnClickListener {
                confirmCall?.apply(this) ?: run {
                    dismiss()
                }
            }
        }
    }

    private fun compatWidth(width: Int, percent: Float): Int {
        val screenWidthDp = context.resources.configuration.screenWidthDp
        val isLargeScreen = screenWidthDp >= 600
        if (isLargeScreen) {
            val maxWidth = context.resources.displayMetrics.density * 411
            return (maxWidth * percent).toInt()
        } else {
            if (width == WindowManager.LayoutParams.MATCH_PARENT) {
                return width
            }
            return (width * percent).toInt()
        }
    }

    fun titleRes(@StringRes res: Int): IMAlert {
        return title(context.resources.getString(res))
    }

    fun title(text: CharSequence): IMAlert {
        this.title = text
        return this
    }

    fun messageRes(@StringRes res: Int): IMAlert {
        return message(context.resources.getString(res))
    }

    fun message(text: CharSequence): IMAlert {
        this.message = text
        return this
    }

    fun messagePadding(rect: Rect): IMAlert {
        this.messagePadding = rect
        return this
    }

    fun messageTextSize(@Px size: Int): IMAlert {
        this.messageTextSize = size
        return this
    }

    fun cancelRes(@StringRes res: Int): IMAlert {
        return cancel(context.resources.getString(res))
    }

    fun cancel(text: CharSequence): IMAlert {
        this.cancel = text
        return this
    }

    fun confirmRes(@StringRes res: Int): IMAlert {
        return confirm(context.resources.getString(res))
    }

    fun confirm(text: CharSequence): IMAlert {
        this.confirm = text
        return this
    }

    fun titleGravity(@GravityInt gravity: Int): IMAlert {
        this.titleGravity = gravity
        return this
    }

    fun messageGravity(@GravityInt gravity: Int): IMAlert {
        this.messageGravity = gravity
        return this
    }

    fun cancelCall(call: ChatFun.Fun1<IMAlert>? = null): IMAlert {
        this.cancelCall = call
        return this
    }

    fun confirmCall(call: ChatFun.Fun1<IMAlert>? = null): IMAlert {
        this.confirmCall = call
        return this
    }

}