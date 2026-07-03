package com.troves.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CountriesNowRequestDto(
    val country: String
)

@Serializable
data class CountriesNowCitiesDto(
    val error: Boolean? = null,
    val msg: String? = null,
    val data: List<String>? = null
)
