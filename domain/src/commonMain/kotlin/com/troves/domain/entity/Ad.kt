package com.troves.domain.entity

/**
 * Promotional banner shown in the Home ad slider.
 * Pure domain model — carries no UI/painter concerns.
 */
data class Ad(
    val id: Long,
    val titleTop: String,
    val titleBottom: String,
    val description: String,
    val buttonText: String = "Shop now",
)
