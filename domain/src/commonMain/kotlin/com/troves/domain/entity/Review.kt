package com.troves.domain.entity

data class Review(
    val id: String,          // == userId (Firestore document id → one review per user per product)
    val userId: String,
    val firstName: String,
    val lastName: String,
    val rating: Int,         // 1..5
    val comment: String,
    val createdAt: Long,     // epoch millis
)
