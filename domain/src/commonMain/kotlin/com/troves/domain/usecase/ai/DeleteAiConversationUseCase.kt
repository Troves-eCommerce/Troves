package com.troves.domain.usecase.ai

import com.troves.domain.repository.AiAssistantRepository
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.utils.Result

class DeleteAiConversationUseCase(
    private val repository: AiAssistantRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(conversationId: String): Boolean {
        val userId = authenticationRepository.getCurrentUserId() ?: return false
        return repository.deleteConversation(userId, conversationId) is Result.Success
    }
}
