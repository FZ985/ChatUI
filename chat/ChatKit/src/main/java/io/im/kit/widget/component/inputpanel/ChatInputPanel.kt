package io.im.kit.widget.component.inputpanel

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import io.im.kit.R
import io.im.kit.callback.ConversationUserCall
import io.im.kit.callback.ConversationUserUpdate
import io.im.kit.config.ChatInputMode
import io.im.kit.config.InputStyle
import io.im.kit.conversation.ConversationFragment
import io.im.kit.conversation.extension.ChatExtensionViewModel
import io.im.kit.databinding.KitComponentInputPanelBinding
import io.im.kit.utils.RouteUtil


/**
 *  author : JFZ
 *  date : 2024/1/26 14:55
 *  description :
 */
class ChatInputPanel : FrameLayout, ConversationUserUpdate {

    private lateinit var mFragment: ConversationFragment
    private var mExtensionViewModel: ChatExtensionViewModel? = null

    private lateinit var userCall: ConversationUserCall

    private lateinit var mInputStyle: InputStyle

    private val binding: KitComponentInputPanelBinding by lazy {
        KitComponentInputPanelBinding.inflate(LayoutInflater.from(context), this, true)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    @SuppressLint("ClickableViewAccessibility")
    fun bindUI(fragment: ConversationFragment, userCall: ConversationUserCall) {
        this.mFragment = fragment
        this.userCall = userCall
        mInputStyle = InputStyle.setType(
            fragment.requireActivity().intent.getIntExtra(
                RouteUtil.InputStyle,
                InputStyle.All.type
            )
        )
        mExtensionViewModel = ViewModelProvider(fragment)[ChatExtensionViewModel::class.java]
        mExtensionViewModel?.inputModeLiveData?.observe(fragment) {
            updateInputMode(it)
        }

        binding.edit.onFocusChangeListener = mOnEditTextFocusChangeListener
        binding.edit.addTextChangedListener(mEditTextWatcher)

        //语音
        binding.voice.setOnClickListener {
            if (mIsVoiceInputMode) {
                mIsVoiceInputMode = false
                mExtensionViewModel?.inputModeLiveData?.postValue(ChatInputMode.TextInput)
                // 切换到文本输入模式后需要弹出软键盘
                binding.edit.postDelayed({ binding.edit.requestFocus() }, 50)
            } else {
                mExtensionViewModel?.inputModeLiveData?.postValue(ChatInputMode.VoiceInput)
                mIsVoiceInputMode = true
            }
        }
        binding.voiceBtn.setOnTouchListener(mOnVoiceBtnTouchListener)

        //emoji
        binding.emoji.setOnClickListener {
            if (mExtensionViewModel?.inputModeLiveData?.value != null
                && mExtensionViewModel?.inputModeLiveData?.value!! == ChatInputMode.EmoticonMode
            ) {
                binding.edit.requestFocus()
                mExtensionViewModel?.inputModeLiveData?.postValue(ChatInputMode.TextInput)
            } else {
                mExtensionViewModel?.inputModeLiveData?.postValue(ChatInputMode.EmoticonMode)
            }
        }

        //插件
        binding.add.setOnClickListener {
            if (mExtensionViewModel?.inputModeLiveData?.value != null
                && mExtensionViewModel
                    ?.inputModeLiveData
                    ?.value!! == ChatInputMode.PluginMode
            ) {
                binding.edit.requestFocus()
                mExtensionViewModel?.inputModeLiveData?.setValue(ChatInputMode.TextInput)
            } else {
                mExtensionViewModel?.inputModeLiveData?.setValue(ChatInputMode.PluginMode)
            }
        }

        //发送
        binding.send.setOnClickListener {
            mExtensionViewModel?.onSendClick()
        }

        initInputStyle(mInputStyle)
    }

    private var mLastTouchY = 0f
    private var mUpDirection = false

    @SuppressLint("ClickableViewAccessibility")
    private val mOnVoiceBtnTouchListener = OnTouchListener { v, event ->
        val mOffsetLimit = 70 * v.context.resources.displayMetrics.density
        //需要拦截请求权限
        if (event.action == MotionEvent.ACTION_DOWN) {
//                if (AudioPlayManager.getInstance().isPlaying()) {
//                    AudioPlayManager.getInstance().stopPlay()
//                }
            // 判断正在视频通话和语音通话中不能进行语音消息发送
            // do -------
//                AudioRecordManager.getInstance()
//                    .startRecord(v.getRootView(), mConversationType, mTargetId)
            mLastTouchY = event.y
            mUpDirection = false
            val textView = v as TextView
            textView.setText(R.string.kit_voice_t2)
            textView.background = ContextCompat.getDrawable(
                v.getContext(),
                R.drawable.kit_voice_btn2
            )
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            if (mLastTouchY - event.y > mOffsetLimit && !mUpDirection) {
//                    AudioRecordManager.getInstance().willCancelRecord()
                mUpDirection = true
                val textView = v as TextView
                textView.setText(R.string.kit_voice_t1)
                textView.background = ContextCompat.getDrawable(
                    v.getContext(),
                    R.drawable.kit_voice_btn1
                )
            } else if (event.y - mLastTouchY > -mOffsetLimit && mUpDirection) {
//                    AudioRecordManager.getInstance().continueRecord()
                mUpDirection = false
                val textView = v as TextView
                textView.background = ContextCompat.getDrawable(
                    v.getContext(),
                    R.drawable.kit_voice_btn2
                )
                textView.setText(R.string.kit_voice_t2)
            }
        } else if (event.action == MotionEvent.ACTION_UP
            || event.action == MotionEvent.ACTION_CANCEL
        ) {
//                AudioRecordManager.getInstance().stopRecord()
            val textView = v as TextView
            textView.setText(R.string.kit_voice_t1)
            textView.background = ContextCompat.getDrawable(
                v.getContext(),
                R.drawable.kit_voice_btn1
            )
        }
        true
    }

    override fun updateConversationUserCall(userCall: ConversationUserCall) {
        this.userCall = userCall
    }

    private val mOnEditTextFocusChangeListener =
        OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (mExtensionViewModel != null
                    && mExtensionViewModel?.inputModeLiveData != null
                ) {
                    mExtensionViewModel?.inputModeLiveData?.postValue(ChatInputMode.TextInput)
                }
                if (!TextUtils.isEmpty(binding.edit.text)) {
                    binding.sendRoot.visibility = VISIBLE
                    binding.add.visibility = GONE
                }
            } else {
                if (mExtensionViewModel != null) {
                    val editText = mExtensionViewModel?.editText
                    if (editText?.text != null && editText.text.isEmpty()) {
                        binding.sendRoot.visibility = GONE
                        if (mInputStyle == InputStyle.All
                            || mInputStyle == InputStyle.Voice_Add
                            || mInputStyle == InputStyle.Emoji_Add
                            || mInputStyle == InputStyle.Add
                        ) {
                            binding.add.visibility = VISIBLE
                        } else {
                            binding.add.visibility = GONE
                        }
                    }
                }
            }
        }

    private val mEditTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (s.isNullOrEmpty()) {
                binding.sendRoot.visibility = GONE
                if (mInputStyle == InputStyle.All
                    || mInputStyle == InputStyle.Voice_Add
                    || mInputStyle == InputStyle.Emoji_Add
                    || mInputStyle == InputStyle.Add
                ) {
                    binding.add.visibility = VISIBLE
                } else {
                    binding.add.visibility = GONE
                }
            } else {
                binding.add.visibility = GONE
                binding.sendRoot.visibility = VISIBLE
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun updateInputMode(mode: ChatInputMode) {
        when (mInputStyle) {
            InputStyle.All -> {
                allInputStyleMode(mode)
            }

            InputStyle.Voice -> {
                voiceStyleMode(mode)
            }

            InputStyle.Emoji -> {
                emojiStyleMode(mode)
            }

            InputStyle.Add -> {
                addStyleMode(mode)
            }

            InputStyle.Voice_Emoji -> {
                voiceEmojiStyleMode(mode)
            }

            InputStyle.Voice_Add -> {
                voiceAddStyleMode(mode)
            }

            InputStyle.Emoji_Add -> {
                emojiAddStyleMode(mode)
            }
        }
    }

    private fun initInputStyle(style: InputStyle) {
        when (style) {
            InputStyle.All -> {
                binding.voice.visibility = View.VISIBLE
                binding.emoji.visibility = View.VISIBLE
                binding.add.visibility = View.VISIBLE
                binding.leftOffset.visibility = View.GONE
            }

            InputStyle.Voice -> {
                binding.voice.visibility = View.VISIBLE
                binding.emoji.visibility = View.GONE
                binding.add.visibility = View.GONE
                binding.leftOffset.visibility = View.GONE
            }

            InputStyle.Emoji -> {
                binding.voice.visibility = View.GONE
                binding.emoji.visibility = View.VISIBLE
                binding.add.visibility = View.GONE
                binding.leftOffset.visibility = View.VISIBLE
            }

            InputStyle.Voice_Emoji -> {
                binding.voice.visibility = View.VISIBLE
                binding.emoji.visibility = View.VISIBLE
                binding.add.visibility = View.GONE
                binding.leftOffset.visibility = View.GONE
            }

            InputStyle.Voice_Add -> {
                binding.voice.visibility = View.VISIBLE
                binding.add.visibility = View.VISIBLE
                binding.emoji.visibility = View.GONE
                binding.leftOffset.visibility = View.GONE
            }

            InputStyle.Emoji_Add -> {
                binding.voice.visibility = View.GONE
                binding.add.visibility = View.VISIBLE
                binding.emoji.visibility = View.VISIBLE
                binding.leftOffset.visibility = View.VISIBLE
            }

            InputStyle.Add -> {
                binding.voice.visibility = View.GONE
                binding.emoji.visibility = View.GONE
                binding.add.visibility = View.VISIBLE
                binding.leftOffset.visibility = View.VISIBLE
            }
        }
    }


    private var mIsVoiceInputMode = false // 语音和键盘切换按钮的当前显示状态是否为键盘模式

    private fun allInputStyleMode(mode: ChatInputMode) {
        when (mode) {
            ChatInputMode.TextInput, ChatInputMode.PluginMode -> {
                if (mode == ChatInputMode.TextInput) {
                    mIsVoiceInputMode = false
                }
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView()
            }

            ChatInputMode.VoiceInput -> {
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_keybroad
                    )
                )
                binding.voiceBtn.visibility = VISIBLE
                binding.edit.visibility = GONE
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
            }

            ChatInputMode.EmoticonMode -> {
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_keybroad
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
            }

            else -> {
                mIsVoiceInputMode = false
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView();
            }
        }
    }

    private fun voiceStyleMode(mode: ChatInputMode) {
        when (mode) {
            ChatInputMode.TextInput -> {
                mIsVoiceInputMode = false
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView()
            }

            ChatInputMode.VoiceInput -> {
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_keybroad
                    )
                )
                binding.voiceBtn.visibility = VISIBLE
                binding.edit.visibility = GONE
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
            }

            else -> {
                mIsVoiceInputMode = false
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView();
            }
        }
    }

    private fun emojiStyleMode(mode: ChatInputMode) {
        when (mode) {
            ChatInputMode.TextInput -> {
                mIsVoiceInputMode = false
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView()
            }

            ChatInputMode.EmoticonMode -> {
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_keybroad
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
            }

            else -> {
                mIsVoiceInputMode = false
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView();
            }
        }
    }

    private fun voiceEmojiStyleMode(mode: ChatInputMode) {
        when (mode) {
            ChatInputMode.TextInput -> {
                mIsVoiceInputMode = false
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView()
            }

            ChatInputMode.VoiceInput -> {
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_keybroad
                    )
                )
                binding.voiceBtn.visibility = VISIBLE
                binding.edit.visibility = GONE
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
            }

            ChatInputMode.EmoticonMode -> {
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_keybroad
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
            }

            else -> {
                mIsVoiceInputMode = false
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView();
            }
        }
    }

    private fun voiceAddStyleMode(mode: ChatInputMode) {
        when (mode) {
            ChatInputMode.TextInput, ChatInputMode.PluginMode -> {
                if (mode == ChatInputMode.TextInput) {
                    mIsVoiceInputMode = false
                }
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView()
            }

            else -> {
                mIsVoiceInputMode = false
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = GONE
                binding.voiceBtn.visibility = GONE
                resetInputView()
            }
        }
    }

    private fun emojiAddStyleMode(mode: ChatInputMode) {
        when (mode) {
            ChatInputMode.TextInput, ChatInputMode.PluginMode -> {
                if (mode == ChatInputMode.TextInput) {
                    mIsVoiceInputMode = false
                }
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView()
            }

            ChatInputMode.EmoticonMode -> {
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_keybroad
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
            }

            else -> {
                mIsVoiceInputMode = false
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView();
            }
        }
    }

    private fun addStyleMode(mode: ChatInputMode) {
        when (mode) {
            ChatInputMode.TextInput, ChatInputMode.PluginMode -> {
                if (mode == ChatInputMode.TextInput) {
                    mIsVoiceInputMode = false
                }
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView()
            }

            else -> {
                mIsVoiceInputMode = false
                binding.voice.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_voice
                    )
                )
                binding.emoji.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.kit_input_emoji
                    )
                )
                binding.edit.visibility = VISIBLE
                binding.voiceBtn.visibility = GONE
                resetInputView();
            }
        }
    }

    fun getEditText(): EditText {
        return binding.edit
    }

    private fun resetInputView() {
        val text = binding.edit.text
        if (text.isNullOrEmpty()) {
            if (mInputStyle == InputStyle.All
                || mInputStyle == InputStyle.Voice_Add
                || mInputStyle == InputStyle.Emoji_Add
                || mInputStyle == InputStyle.Add
            ) {
                binding.add.visibility = VISIBLE
            } else {
                binding.add.visibility = GONE
            }
            binding.sendRoot.visibility = GONE
        } else {
            binding.sendRoot.visibility = VISIBLE
            binding.add.visibility = GONE
        }
    }

    fun onDestroy() {
        mExtensionViewModel = null
    }
}