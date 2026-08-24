package io.ai.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.ai.chat.databinding.AiItemChatMessageAiBinding
import io.ai.chat.databinding.AiItemChatMessageUserBinding
import io.ai.chat.markdown.MarkdownParseCache
import io.ai.chat.model.AIChatMessage


/**
 * by DAD FZ
 * 2026/8/20
 * desc：
 **/
class AIChatMessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<AIChatMessage>()

    /** 全局 Markdown 解析缓存，由 ChatFragment 设置 */
    var parseCache: MarkdownParseCache? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return stableItemId(messages[position].id)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == viewTypeUser) {
            ItemChatUserHolder(AiItemChatMessageUserBinding.inflate(inflater, parent, false))
        } else {
            val binding = AiItemChatMessageAiBinding.inflate(inflater, parent, false)
            // 注入全局解析缓存，让 MarkdownView 在绑定时优先使用预解析结果
//            binding.markdownContent.setGlobalParseCache(parseCache)
            ItemChatAIHolder(binding)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is ItemChatUserHolder -> holder.bind(messages[position], position)
            is ItemChatAIHolder -> holder.bind(messages[position], position)
        }
    }


    private inner class ItemChatUserHolder(private val binding: AiItemChatMessageUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

        }

        fun bind(message: AIChatMessage, position: Int) {

        }
    }

    private inner class ItemChatAIHolder(private val binding: AiItemChatMessageAiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

        }

        fun bind(message: AIChatMessage, position: Int) {

        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    companion object {
        val viewTypeAI = 0
        val viewTypeUser = 1

        /** 优先把 UUID 形式的消息 ID 映射为稳定 long，非 UUID 时回退 FNV 风格折叠，避免 hashCode 截断碰撞。 */
        fun stableItemId(id: String): Long {
            return runCatching {
                val uuid = java.util.UUID.fromString(id)
                uuid.mostSignificantBits xor uuid.leastSignificantBits
            }.getOrElse {
                id.fold(1125899906842597L) { hash, char -> hash * 31 + char.code }
            }
        }
    }
}