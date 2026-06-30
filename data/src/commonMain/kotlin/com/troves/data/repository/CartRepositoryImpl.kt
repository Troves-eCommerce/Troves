package com.troves.data.repository

import com.troves.domain.entity.CartItem
import com.troves.domain.entity.Product
import com.troves.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CartRepositoryImpl : CartRepository {

    private val mutex = Mutex()
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    override val cartItems: Flow<List<CartItem>> = _cartItems.asStateFlow()

    override suspend fun addToCart(product: Product) {
        mutex.withLock {
            val current = _cartItems.value.toMutableList()
            val existingIndex = current.indexOfFirst { it.product.id == product.id }
            if (existingIndex >= 0) {
                current[existingIndex] = current[existingIndex].copy(
                    quantity = current[existingIndex].quantity + 1
                )
            } else {
                current.add(CartItem(product = product, quantity = 1))
            }
            _cartItems.value = current
        }
    }

    override suspend fun removeFromCart(productId: Long) {
        mutex.withLock {
            _cartItems.value = _cartItems.value.filter { it.product.id != productId }
        }
    }

    override suspend fun updateQuantity(productId: Long, quantity: Int) {
        mutex.withLock {
            _cartItems.value = _cartItems.value.map { item ->
                if (item.product.id == productId) item.copy(quantity = quantity) else item
            }
        }
    }

    override suspend fun clearCart() {
        mutex.withLock {
            _cartItems.value = emptyList()
        }
    }
}
