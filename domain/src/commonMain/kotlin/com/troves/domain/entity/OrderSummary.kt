package com.troves.domain.entity

data class OrderSummary(
    val id: String,
    val number: Int,
    val processedAt: String,
    val financialStatus: String?,
    val fulfillmentStatus: String?,
    val total: CartMoney,
    val itemCount: Int,
    val thumbnailUrl: String?,
)
