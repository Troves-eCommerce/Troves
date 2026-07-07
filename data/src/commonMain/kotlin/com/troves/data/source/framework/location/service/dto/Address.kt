package com.troves.data.source.framework.location.service.dto


import com.troves.domain.entity.LocationAddress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    @SerialName("city")
    val city: String?,
    @SerialName("country")
    val country: String?,
    @SerialName("country_code")
    val countryCode: String?,
    @SerialName("house_number")
    val houseNumber: String?,
    @SerialName("neighbourhood")
    val neighbourhood: String?,
    @SerialName("postcode")
    val postcode: String?,
    @SerialName("road")
    val road: String?,
    @SerialName("state")
    val state: String?,
    @SerialName("suburb")
    val suburb: String?
) {
    fun toDomain(): LocationAddress = LocationAddress(
        city = city ?: "Unknown",
        country = country ?: "Unknown",
        countryCode = countryCode ?: "Unknown",
        houseNumber = houseNumber ?: "Unknown",
        neighbourhood = neighbourhood ?: "Unknown",
        postcode = postcode ?: "Unknown",
        road = road ?: "Unknown",
        state = state ?: "Unknown",
        suburb = suburb ?: "Unknown"
    )
}