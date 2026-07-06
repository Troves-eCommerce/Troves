package com.troves.data.repository

import com.troves.data.mapper.toDomain
import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.ai.AiDataSource
import com.troves.data.source.remote.ai.dto.AiChatRequestDto
import com.troves.data.source.remote.ai.dto.AiHistoryTurnDto
import com.troves.data.source.remote.service.ktor.dto.AiChatMessageDto
import com.troves.data.source.remote.service.ktor.dto.AiConversationDto
import com.troves.data.source.remote.service.ktor.dto.AiStoredProductDto
import com.troves.domain.entity.AiChatReply
import com.troves.domain.entity.AiChatTurn
import com.troves.domain.entity.AiConversation
import com.troves.domain.entity.AiStoredMessage
import com.troves.domain.entity.AiStoredProduct
import com.troves.domain.repository.AiAssistantRepository
import com.troves.domain.utils.Result
import com.troves.domain.utils.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiAssistantRepositoryImpl(
    private val dataSource: AiDataSource,
    private val preferences: TrovesPreferences,
    private val remoteDatasource: RemoteDatasource,
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


    override suspend fun getConversations(userId: String): Result<List<AiConversation>> {
        return when (val result = remoteDatasource.getAiChats(userId)) {
            is Result.Success ->
                Result.Success(result.value.map { it.toDomain() }.sortedByDescending { it.updatedAt })
            is Result.Error -> Result.Error(result.throwable)
            Result.Loading -> Result.Loading
        }
    }

    override suspend fun saveConversation(userId: String, conversation: AiConversation): Result<String> =
        remoteDatasource.saveAiChat(userId, conversation.toDto())

    override suspend fun deleteConversation(userId: String, conversationId: String): Result<Unit> =
        remoteDatasource.deleteAiChat(userId, conversationId)

    private fun AiConversationDto.toDomain() = AiConversation(
        id = id,
        title = title,
        updatedAt = updatedAt,
        messages = messages.map { it.toDomain() },
    )

    private fun AiChatMessageDto.toDomain() = AiStoredMessage(
        id = id,
        role = sender,
        text = text,
        isSuggestion = isSuggestion,
        clarifying = clarifying,
        declined = declined,
        products = products.map { it.toDomain() },
    )

    private fun AiStoredProductDto.toDomain() = AiStoredProduct(
        id = id,
        handle = handle,
        title = title,
        imageUrl = imageUrl.ifBlank { null },
        priceFormatted = priceFormatted,
        priceAmount = priceAmount,
        why = why,
    )

    private fun AiConversation.toDto() = AiConversationDto(
        id = id,
        title = title,
        updatedAt = updatedAt,
        messages = messages.map { it.toDto() },
    )

    private fun AiStoredMessage.toDto() = AiChatMessageDto(
        id = id,
        sender = role,
        text = text,
        isSuggestion = isSuggestion,
        clarifying = clarifying,
        declined = declined,
        products = products.map { it.toDto() },
    )

    private fun AiStoredProduct.toDto() = AiStoredProductDto(
        id = id,
        handle = handle,
        title = title,
        imageUrl = imageUrl.orEmpty(),
        priceFormatted = priceFormatted,
        priceAmount = priceAmount,
        why = why,
    )
}
