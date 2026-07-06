package com.troves.data.source.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class UserProfileDto(
    val cartId: String? = null,
    val survey: SurveyAnswersDto? = null,
)

@Serializable
data class SurveyAnswersDto(
    val favoriteCategories: List<String> = emptyList(),
    val favoriteBrands: List<String> = emptyList(),
    val preferredPriceRange: String = "",
    val shoppingStyle: String = "",
    val favoriteColors: List<String> = emptyList(),
    val gender: String = "",
    val ageGroup: String = "",
    val shoppingFrequency: String = "",
    val completed: Boolean = true,
    val completedAt: String = ""
)
