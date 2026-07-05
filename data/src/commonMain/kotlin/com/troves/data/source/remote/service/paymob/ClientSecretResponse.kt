package com.troves.data.source.remote.service.paymob


import com.troves.domain.entity.ClientSecret
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientSecretResponse(
    @SerialName("clientSecret")
    val clientSecret: String?,
    @SerialName("draftOrderId")
    val draftOrderId: Long?,
    @SerialName("draftOrderName")
    val draftOrderName: String?,
    @SerialName("fxRateUsed")
    val fxRateUsed: Double?
)


fun ClientSecretResponse.toDomain(): ClientSecret = ClientSecret(
    clientSecret = clientSecret,
    draftOrderId = draftOrderId,
    draftOrderName = draftOrderName,
    fxRateUsed = fxRateUsed
)