package com.troves.domain.entity

data class AiChatReply(
    val reasoning: String,
    val clarifyingQuestion: String?,
    val noMatch: Boolean,
    val declined: Boolean,
    val suggested: Boolean,
    val products: List<AiRecommendedProduct>,
)

data class AiRecommendedProduct(
    val id: String,
    val handle: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val priceFormatted: String,
    val priceAmount: String,
    val available: Boolean,
    val why: String,
)

data class AiChatTurn(val role: String, val text: String)
