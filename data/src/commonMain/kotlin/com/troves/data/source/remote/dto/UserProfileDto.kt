package com.troves.data.source.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class UserProfileDto(
    val cartId: String? = null,
)
