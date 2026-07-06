package com.troves.domain.repository

import com.troves.domain.entity.AiChatReply
import com.troves.domain.entity.AiChatTurn
import com.troves.domain.entity.AiConversation
import com.troves.domain.utils.Result

interface AiAssistantRepository {
    suspend fun sendMessage(
        message: String,
        imageBase64: String? = null,
        history: List<AiChatTurn> = emptyList(),
    ): Result<AiChatReply>
    suspend fun getConversations(userId: String): Result<List<AiConversation>>

    suspend fun saveConversation(userId: String, conversation: AiConversation): Result<String>

    suspend fun deleteConversation(userId: String, conversationId: String): Result<Unit>
}
