package com.troves.data.source.remote.service.shopify_dtos


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token(
    @SerialName("customerAccessTokenCreate")
    val customerAccessTokenCreate: CustomerAccessTokenCreate?
)