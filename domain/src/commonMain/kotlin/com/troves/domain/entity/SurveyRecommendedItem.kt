package com.troves.domain.entity

/**
 * A product recommended by the AI based on the user's survey answers.
 * Maps directly onto [Product] for display — separated here to carry
 * the recommendation "reason" without polluting the core Product entity.
 */
data class SurveyRecommendedItem(
    val id: String,
    val title: String,
    val vendor: String,
    val imageUrl: String?,
    val price: String,
    val status: String = "active",
)
