package com.troves.domain.home

/**
 * A product category surfaced in the Home "Categories" rail.
 */
data class Category(
    val id: Long,
    val name: String,
    val imageUrl: String? = null,
)
