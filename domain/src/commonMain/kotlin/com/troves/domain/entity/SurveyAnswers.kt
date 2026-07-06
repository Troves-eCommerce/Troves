package com.troves.domain.entity

data class SurveyAnswers(
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
