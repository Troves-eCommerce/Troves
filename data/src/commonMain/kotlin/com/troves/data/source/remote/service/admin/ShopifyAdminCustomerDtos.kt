package com.troves.data.source.remote.service.admin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdminCustomerSearchResponse(
    val customers: List<AdminCustomerDto> = emptyList(),
)

@Serializable
internal data class AdminSingleCustomerResponse(
    val customer: AdminCustomerDto? = null,
)

@Serializable
internal data class AdminCustomerDto(
    val id: Long,
    val email: String? = null,
)

@Serializable
internal data class AdminCustomerUpdateRequest(
    val customer: AdminCustomerUpdateBody,
)

@Serializable
internal data class AdminCustomerUpdateBody(
    val id: Long,
    val password: String,
    @SerialName("password_confirmation") val passwordConfirmation: String,
)
