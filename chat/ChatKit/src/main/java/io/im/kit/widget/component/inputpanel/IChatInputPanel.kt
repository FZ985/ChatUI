package io.im.kit.widget.component.inputpanel

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import io.im.kit.R
import io.im.kit.config.ChatInputMode
import io.im.kit.config.InputStyle
import io.im.kit.databinding.KitComponentInputPanelBinding


/**
 *  author : JFZ
 *  date : 2024/1/30 13:56
 *  description :
 */
class IChatInputPanel : FrameLayout {

    private lateinit var binding: KitComponentInputPanelBinding

    private lateinit var mInputStyle: InputStyle

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        binding = KitComponentInputPanelBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setInputStyle(style: InputStyle) {
        mInputStyle = style
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

    private val mOnEditTextFocusChangeListener =
        OnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                if (mExtensionViewModel != null
//                    && mExtensionViewModel?.inputModeLiveData != null
//                ) {
//                    mExtensionViewModel?.inputModeLiveData?.postValue(ChatInputMode.TextInput)
//                }
//                if (!TextUtils.isEmpty(binding.edit.text)) {
//                    binding.sendRoot.visibility = VISIBLE
//                    binding.add.visibility = GONE
//                }
//            } else {
//                if (mExtensionViewModel != null) {
//                    val editText = mExtensionViewModel?.editText
//                    if (editText?.text != null && editText.text.isEmpty()) {
//                        binding.sendRoot.visibility = GONE
//                        if (mInputStyle == InputStyle.All
//                            || mInputStyle == InputStyle.Voice_Add
//                            || mInputStyle == InputStyle.Emoji_Add
//                            || mInputStyle == InputStyle.Add
//                        ) {
//                            binding.add.visibility = VISIBLE
//                        } else {
//                            binding.add.visibility = GONE
//                        }
//                    }
//                }
//            }
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
}