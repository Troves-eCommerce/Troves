package com.troves.data.source.remote.location.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CountriesNowIsoResponse(
    val error: Boolean? = null,
    val msg: String? = null,
    val data: List<CountriesNowCountryDto>? = null,
)

@Serializable
data class CountriesNowCountryDto(
    val name: String? = null,
    @SerialName("Iso2") val iso2: String? = null,
    @SerialName("Iso3") val iso3: String? = null,
)

@Serializable
data class CountriesNowCitiesResponse(
    val error: Boolean? = null,
    val msg: String? = null,
    val data: List<String>? = null,
)
