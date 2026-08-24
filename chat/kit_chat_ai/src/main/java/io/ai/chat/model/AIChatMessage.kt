package io.ai.chat.model

import java.util.UUID


/**
 * by DAD FZ
 * 2026/8/20
 * desc：
 **/
data class AIChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: ChatRole,
    val content: String,
    val toolCallId: String? = null,
    val toolName: String? = null,
    val toolArguments: String? = null,
    val toolCalls: List<AiToolCall> = emptyList(),
    val assistantParts: List<AssistantPart> = emptyList(),
    val attachments: List<ChatAttachment> = emptyList(),
    val excludeFromConversation: Boolean = false,
    val isContextSummary: Boolean = false,
    val contextSummarySourceIds: List<String> = emptyList()
)