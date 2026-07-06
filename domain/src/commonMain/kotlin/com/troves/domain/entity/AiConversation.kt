package com.troves.domain.entity

data class AiConversation(
    val id: String,
    val title: String,
    val updatedAt: Long,
    val messages: List<AiStoredMessage>,
)

data class AiStoredMessage(
    val id: String,
    val role: String,
    val text: String,
    val isSuggestion: Boolean = false,
    val clarifying: Boolean = false,
    val declined: Boolean = false,
    val products: List<AiStoredProduct> = emptyList(),
)

data class AiStoredProduct(
    val id: String,
    val handle: String,
    val title: String,
    val imageUrl: String?,
    val priceFormatted: String,
    val priceAmount: String,
    val why: String,
)
