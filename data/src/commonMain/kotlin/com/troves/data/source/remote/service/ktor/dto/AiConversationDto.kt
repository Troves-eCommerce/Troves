package com.troves.data.source.remote.service.ktor.dto

import kotlinx.serialization.Serializable

@Serializable
data class AiConversationDto(
    val id: String = "",
    val title: String = "",
    val updatedAt: Long = 0L,
    val messages: List<AiChatMessageDto> = emptyList(),
)

@Serializable
data class AiChatMessageDto(
    val id: String = "",
    val sender: String = "",
    val text: String = "",
    val isSuggestion: Boolean = false,
    val clarifying: Boolean = false,
    val declined: Boolean = false,
    val products: List<AiStoredProductDto> = emptyList(),
)

@Serializable
data class AiStoredProductDto(
    val id: String = "",
    val handle: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val priceFormatted: String = "",
    val priceAmount: String = "",
    val why: String = "",
)
