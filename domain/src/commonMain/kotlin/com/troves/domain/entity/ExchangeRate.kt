package com.troves.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRate(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
