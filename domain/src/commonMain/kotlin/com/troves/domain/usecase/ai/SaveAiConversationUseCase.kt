package com.troves.domain.usecase.ai

import com.troves.domain.entity.AiConversation
import com.troves.domain.repository.AiAssistantRepository
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.utils.Result

class SaveAiConversationUseCase(
    private val repository: AiAssistantRepository,
    private val authenticationRepository: AuthenticationRepository,
) {

    suspend operator fun invoke(conversation: AiConversation): String? {
        val userId = authenticationRepository.getCurrentUserId() ?: return null
        return when (val result = repository.saveConversation(userId, conversation)) {
            is Result.Success -> result.value
            else -> null
        }
    }
}
