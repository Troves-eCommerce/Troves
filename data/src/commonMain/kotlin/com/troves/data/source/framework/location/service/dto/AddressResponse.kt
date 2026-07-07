package com.troves.data.source.framework.location.service.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressResponse(
    @SerialName("address")
    val address: Address?,
    @SerialName("boundingbox")
    val boundingbox: List<String?>?,
    @SerialName("display_name")
    val displayName: String?,
    @SerialName("lat")
    val lat: String?,
    @SerialName("licence")
    val licence: String?,
    @SerialName("lon")
    val lon: String?,
    @SerialName("osm_id")
    val osmId: String?,
    @SerialName("osm_type")
    val osmType: String?,
    @SerialName("place_id")
    val placeId: String?
)