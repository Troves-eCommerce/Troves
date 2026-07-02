package com.troves.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestCountriesV5Response(
    val data: RestCountriesV5Data? = null
)

@Serializable
data class RestCountriesV5Data(
    val objects: List<RestCountryDto> = emptyList()
)

@Serializable
data class RestCountryDto(
    @SerialName("names.common")
    val nameCommon: String? = null,
    @SerialName("names")
    val names: RestCountryNameDto? = null
)

@Serializable
data class RestCountryNameDto(
    val common: String? = null
)
