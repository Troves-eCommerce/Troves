package com.troves.domain.repository

import com.troves.domain.entity.CartItem
import com.troves.domain.entity.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    val cartItems: Flow<List<CartItem>>
    suspend fun addToCart(product: Product)
    suspend fun removeFromCart(productId: Long)
    suspend fun updateQuantity(productId: Long, quantity: Int)
    suspend fun clearCart()
}
