package com.troves.data.source.remote.service.shopify_dtos


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerAccessToken(
    @SerialName("accessToken")
    val accessToken: String?,
    @SerialName("expiresAt")
    val expiresAt: String?
)