package com.troves.data.source.remote.service.ktor.dto

import kotlinx.serialization.Serializable

@Serializable
data class SingleProductResponse(
    val product: ProductDto?
)