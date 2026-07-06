package com.troves.domain.usecase.ai

import com.troves.domain.entity.AiConversation

sealed interface AiHistoryListResult {
    data class Success(val conversations: List<AiConversation>) : AiHistoryListResult
    data object RequiresLogin : AiHistoryListResult
    data class Error(val throwable: Throwable) : AiHistoryListResult
}
