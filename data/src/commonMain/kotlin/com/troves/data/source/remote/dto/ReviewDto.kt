package com.troves.data.source.remote.dto

import kotlinx.serialization.Serializable

/** Firestore document under products/{productId}/reviews/{userId}. All-default params for deserialize. */
@Serializable
data class ReviewDto(
    val id: String = "",
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: Long = 0L,
)
