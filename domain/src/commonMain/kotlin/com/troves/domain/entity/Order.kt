package com.troves.domain.entity


data class Order(
    val id: String,
    val number: Int,
    val name: String,
    val processedAt: String,
    val financialStatus: String?,
    val fulfillmentStatus: String?,
    val subtotal: CartMoney,
    val total: CartMoney,
    val shippingAddress: Address?,
    val statusUrl: String,
    val lineItems: List<OrderLineItem> = emptyList(),
)

data class OrderLineItem(
    val title: String,
    val variantTitle: String?,
    val imageUrl: String?,
    val quantity: Int,
    val price: CartMoney,
)
