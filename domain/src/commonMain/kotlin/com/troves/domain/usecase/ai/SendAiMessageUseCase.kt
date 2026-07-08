package com.troves.domain.usecase.ai

import com.troves.domain.entity.AiChatReply
import com.troves.domain.entity.AiChatTurn
import com.troves.domain.repository.AiAssistantRepository
import com.troves.domain.utils.NoConnectionException
import com.troves.domain.utils.Result
import com.troves.domain.utils.connectivity.ConnectivityObserver
import com.troves.domain.utils.connectivity.ConnectivityStatus

class SendAiMessageUseCase(
    private val repository: AiAssistantRepository,
    private val connectivityObserver: ConnectivityObserver,
) {
    suspend operator fun invoke(
        message: String,
        imageBase64: String? = null,
        history: List<AiChatTurn> = emptyList(),
    ): Result<AiChatReply> {
        if (connectivityObserver.currentStatus() != ConnectivityStatus.Available) {
            return Result.Error(NoConnectionException())
        }
        return repository.sendMessage(message.trim(), imageBase64, history)
    }
}
