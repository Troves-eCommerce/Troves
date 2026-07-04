package com.troves.data.source.remote.ai.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiChatRequestDto(
    val deviceId: String,
    val message: String = "",
    val imageBase64: String? = null,
    val history: List<AiHistoryTurnDto> = emptyList(),
)

@Serializable
data class AiHistoryTurnDto(
    val role: String, // "user" | "assistant"
    val text: String,
)

@Serializable
data class AiChatResponseDto(
    val reasoning: String = "",
    @SerialName("clarifying_question") val clarifyingQuestion: String? = null,
    @SerialName("no_match") val noMatch: Boolean = false,
    val declined: Boolean = false,
    val suggested: Boolean = false,
    val products: List<AiProductDto> = emptyList(),
    val meta: AiMetaDto? = null,
)

@Serializable
data class AiProductDto(
    val id: String? = null,
    val handle: String? = null,
    val title: String? = null,
    val description: String? = null,
    val featuredImage: String? = null,
    val price: AiPriceDto? = null,
    val available: Boolean = false,
    val why: String = "",
)

@Serializable
data class AiPriceDto(
    val amount: String? = null,
    val currencyCode: String? = null,
    val formatted: String? = null,
)

@Serializable
data class AiMetaDto(
    val model: String? = null,
    val mock: Boolean = false,
    val candidateCount: Int = 0,
    val fallbackUsed: Boolean = false,
)

@Serializable
data class AiErrorDto(
    val error: String = "",
    val message: String? = null,
    val retryAfter: Int? = null,
)
