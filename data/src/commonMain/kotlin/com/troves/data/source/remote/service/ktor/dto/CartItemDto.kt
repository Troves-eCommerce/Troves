package com.troves.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItemDto(
    val id: Long = 0,
    val title: String = "",
    val vendor: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val status: String = "",
    val quantity: Int = 1,
)
