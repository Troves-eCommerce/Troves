package com.troves.domain.repository

import com.troves.domain.entity.Address
import com.troves.domain.entity.Cart
import com.troves.domain.entity.Order


interface OrderRepository {
    suspend fun getDefaultAddress(): Address?

    suspend fun getOrders(): List<Order>
    suspend fun getOrderById(orderId: String): Order?

    suspend fun placeCodOrder(cart: Cart, address: Address): String

    suspend fun attachAddressToCart(cartId: String, address: Address)
}
