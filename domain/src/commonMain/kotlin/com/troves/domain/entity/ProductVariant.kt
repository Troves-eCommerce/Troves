package com.troves.domain.entity


data class ProductVariant(
    val variantId: String,
    val title: String,
    val price: String,
    val available: Boolean,
    val inventoryQuantity: Int?,
    val selectedOptions: Map<String, String>,
)
