package com.troves.data.source.remote.service.shopify_dtos


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    @SerialName("email")
    val email: String?,
    @SerialName("firstName")
    val firstName: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("lastName")
    val lastName: String?
)