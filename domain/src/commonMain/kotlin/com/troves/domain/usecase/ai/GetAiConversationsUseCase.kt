package com.troves.domain.usecase.ai

import com.troves.domain.repository.AiAssistantRepository
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.utils.Result

class GetAiConversationsUseCase(
    private val repository: AiAssistantRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(): AiHistoryListResult {
        if (!authenticationRepository.isLoggedIn()) return AiHistoryListResult.RequiresLogin
        val userId = authenticationRepository.getCurrentUserId()
            ?: return AiHistoryListResult.RequiresLogin
        return when (val result = repository.getConversations(userId)) {
            is Result.Success -> AiHistoryListResult.Success(result.value)
            is Result.Error -> AiHistoryListResult.Error(result.throwable)
            Result.Loading -> AiHistoryListResult.Error(IllegalStateException("Unexpected loading state"))
        }
    }
}
