package io.ai.chat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import io.ai.chat.databinding.AiFragmentChatBinding
import io.ai.chat.viewmodel.AiChatMessageViewModel
import io.im.uicommon.base.ChatBaseFragment


/**
 * by DAD FZ
 * 2026/8/20
 * desc：
 **/
class AIChatFragment : ChatBaseFragment() {

    private val binding: AiFragmentChatBinding by lazy {
        AiFragmentChatBinding.inflate(layoutInflater)
    }

    var onLoad: (() -> Unit)? = null

    private val viewModel: AiChatMessageViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.send.setOnClickListener {
            viewModel.sendMessage()
        }
        onLoad?.invoke()
    }

}