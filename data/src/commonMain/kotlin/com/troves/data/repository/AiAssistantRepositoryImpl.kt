package com.troves.data.repository

import com.troves.data.mapper.toDomain
import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.ai.AiDataSource
import com.troves.data.source.remote.ai.dto.AiChatRequestDto
import com.troves.data.source.remote.ai.dto.AiHistoryTurnDto
import com.troves.domain.entity.AiChatReply
import com.troves.domain.entity.AiChatTurn
import com.troves.domain.repository.AiAssistantRepository
import com.troves.domain.utils.Result
import com.troves.domain.utils.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiAssistantRepositoryImpl(
    private val dataSource: AiDataSource,
    private val preferences: TrovesPreferences,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : AiAssistantRepository {

    override suspend fun sendMessage(
        message: String,
        imageBase64: String?,
        history: List<AiChatTurn>,
    ): Result<AiChatReply> = withContext(coroutineDispatcher) {
        val deviceId = preferences.getOrCreateDeviceId()
        dataSource.chat(
            AiChatRequestDto(
                deviceId = deviceId,
                message = message,
                imageBase64 = imageBase64,
                history = history.map { AiHistoryTurnDto(it.role, it.text) },
            )
        ).map { it.toDomain() }
    }
}
