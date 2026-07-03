package com.troves.data.source.remote.service.shopify_dtos


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShopifyUserResponse(
    @SerialName("customer")
    val customer: Customer?,
    @SerialName("customerUserErrors")
    val customerUserErrors: List<CustomerUserError>? = null
)