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
    val categories: List<String> = emptyList(),
    val preferredPriceRange: String = "",
    val shoppingStyle: String = "",
    val gender: String = "",
)

@Serializable
data class SurveyRecommendationResponseDto(
    val products: List<SurveyRecommendedProductDto> = emptyList(),
)

@Serializable
data class SurveyRecommendedProductDto(
    val id: String = "",
    val title: String = "",
    val vendor: String = "",
    @SerialName("image_url") val imageUrl: String? = null,
    val price: String = "",
    val status: String = "active",
)
