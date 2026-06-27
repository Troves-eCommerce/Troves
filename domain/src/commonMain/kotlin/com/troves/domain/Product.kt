package com.troves.domain

/**
 * Lightweight domain model — only the fields the UI actually needs.
 * Keeps the presentation layer completely decoupled from network DTOs.
 */
data class Product(
    val id: Long,
    val title: String,
    val vendor: String,
    val price: String,
    val imageUrl: String?,
    val status: String
)
