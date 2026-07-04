package com.troves.domain.repository

import com.troves.domain.entity.AiChatReply
import com.troves.domain.entity.AiChatTurn
import com.troves.domain.utils.Result

interface AiAssistantRepository {
    suspend fun sendMessage(
        message: String,
        imageBase64: String? = null,
        history: List<AiChatTurn> = emptyList(),
    ): Result<AiChatReply>
}
