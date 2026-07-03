package com.troves.domain.repository

import com.troves.domain.entity.Cart
import kotlinx.coroutines.flow.Flow


interface CartRepository {
    val cart: Flow<Cart?>

    suspend fun addToCart(variantId: String, quantity: Int): Cart
    suspend fun updateQuantity(lineId: String, quantity: Int): Cart
    suspend fun removeFromCart(lineId: String): Cart

    suspend fun removeAllItems(): Cart

    suspend fun applyDiscountCodes(codes: List<String>): Cart

    suspend fun refreshCart()

    suspend fun clearCart()

    suspend fun clearLocal()
}
