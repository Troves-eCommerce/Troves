package com.troves.data.repository

import com.troves.data.local.database.CartDao
import com.troves.data.local.database.CartEntity
import com.troves.domain.entity.CartItem
import com.troves.domain.entity.Product
import com.troves.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepositoryImpl(
    private val cartDao: CartDao
) : CartRepository {

    override val cartItems: Flow<List<CartItem>> = cartDao.getAllCartItems().map { list ->
        list.map { it.toDomain() }
    }

    override suspend fun addToCart(product: Product) {
        val existingItem = cartDao.getCartItem(product.id)
        if (existingItem != null) {
            cartDao.updateQuantity(product.id, existingItem.quantity + 1)
        } else {
            cartDao.insertCartItem(product.toEntity(quantity = 1))
        }
    }

    override suspend fun removeFromCart(productId: Long) {
        cartDao.deleteCartItem(productId)
    }

    override suspend fun updateQuantity(productId: Long, quantity: Int) {
        if (quantity > 0) {
            cartDao.updateQuantity(productId, quantity)
        } else {
            cartDao.deleteCartItem(productId)
        }
    }

    override suspend fun clearCart() {
        cartDao.clearAll()
    }

    private fun CartEntity.toDomain() = CartItem(
        product = Product(
            id = id,
            title = title,
            vendor = vendor,
            price = price,
            imageUrl = imageUrl,
            status = status,
        ),
        quantity = quantity
    )

    private fun Product.toEntity(quantity: Int) = CartEntity(
        id = id,
        title = title,
        vendor = vendor,
        price = price,
        imageUrl = imageUrl,
        status = status,
        quantity = quantity
    )
}
