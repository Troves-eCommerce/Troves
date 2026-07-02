package com.troves.domain.repository

import com.troves.domain.entity.CartItem
import com.troves.domain.entity.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    val cartItems: Flow<List<CartItem>>
    suspend fun addToCart(product: Product, userId: String)
    suspend fun removeFromCart(productId: Long, userId: String)
    suspend fun updateQuantity(productId: Long, quantity: Int, userId: String)
    suspend fun clearCart(userId: String)
    suspend fun syncFromRemote(userId: String)
    suspend fun syncLocalOfflineCart(userId: String)
    suspend fun clearLocal()
}
