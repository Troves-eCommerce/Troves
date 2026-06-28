package com.troves.domain.home

/**
 * A shoppable brand surfaced in the Home "Brands" rail.
 */
data class Brand(
    val id: Long,
    val name: String,
    val logoUrl: String? = null,
)
