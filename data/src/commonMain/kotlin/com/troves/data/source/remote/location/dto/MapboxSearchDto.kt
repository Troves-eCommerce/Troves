package com.troves.data.source.remote.location.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MapboxSuggestResponse(
    @SerialName("suggestions") val suggestions: List<MapboxSuggestion> = emptyList()
)

@Serializable
data class MapboxSuggestion(
    @SerialName("name") val name: String,
    @SerialName("mapbox_id") val mapboxId: String,
    @SerialName("feature_type") val featureType: String = "",
    @SerialName("place_formatted") val placeFormatted: String = "",
    @SerialName("address") val address: String = "",
    @SerialName("full_address") val fullAddress: String = ""
)

@Serializable
data class MapboxRetrieveResponse(
    @SerialName("features") val features: List<MapboxFeature> = emptyList()
)

@Serializable
data class MapboxFeature(
    @SerialName("type") val type: String = "",
    @SerialName("geometry") val geometry: MapboxGeometry,
    @SerialName("properties") val properties: MapboxProperties
)

@Serializable
data class MapboxGeometry(
    @SerialName("type") val type: String = "",
    @SerialName("coordinates") val coordinates: List<Double> = emptyList() // [longitude, latitude]
)

@Serializable
data class MapboxProperties(
    @SerialName("name") val name: String = "",
    @SerialName("place_formatted") val placeFormatted: String = "",
    @SerialName("full_address") val fullAddress: String = "",
    @SerialName("coordinates") val coordinates: MapboxCoordinates? = null
)

@Serializable
data class MapboxCoordinates(
    @SerialName("longitude") val longitude: Double,
    @SerialName("latitude") val latitude: Double
)
