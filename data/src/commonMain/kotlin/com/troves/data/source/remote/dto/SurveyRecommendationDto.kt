package com.troves.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for the AI survey recommendation endpoint.
 * The real endpoint URL will be provided later; the implementation in
 * [RemoteDatasourceImpl.getSurveyRecommendations] currently returns a
 * placeholder list so the full feature pipeline can be exercised end-to-end.
 */
@Serializable
data class SurveyRecommendationRequestDto(
    val cartId: String? = null,
    val survey: com.troves.data.source.remote.dto.SurveyAnswersDto? = null,
)

@Serializable
data class SurveyRecommendationResponseDto(
    val reasoning: String = "",
    val language: String = "en",
    @SerialName("clarifying_question") val clarifyingQuestion: String? = null,
    @SerialName("no_match") val noMatch: Boolean = false,
    val suggested: Boolean = false,
    val followups: List<String> = emptyList(),
    val products: List<SurveyRecommendedProductDto> = emptyList(),
    val cartId: String? = null
)

@Serializable
data class SurveyRecommendedProductDto(
    val id: String = "",
    val handle: String = "",
    val title: String = "",
    val description: String = "",
    val featuredImage: String? = null,
    val price: com.troves.data.source.remote.ai.dto.AiPriceDto? = null,
    val available: Boolean = true,
    val why: String = ""
)
