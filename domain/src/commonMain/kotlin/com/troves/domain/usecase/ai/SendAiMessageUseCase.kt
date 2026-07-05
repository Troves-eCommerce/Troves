package com.troves.domain.usecase.ai

import com.troves.domain.entity.AiChatReply
import com.troves.domain.entity.AiChatTurn
import com.troves.domain.repository.AiAssistantRepository
import com.troves.domain.utils.Result

class SendAiMessageUseCase(
    private val repository: AiAssistantRepository,
) {
    suspend operator fun invoke(
        message: String,
        imageBase64: String? = null,
        history: List<AiChatTurn> = emptyList(),
    ): Result<AiChatReply> = repository.sendMessage(message.trim(), imageBase64, history)
}
