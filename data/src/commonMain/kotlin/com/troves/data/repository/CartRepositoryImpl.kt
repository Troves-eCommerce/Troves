package com.troves.data.repository

import com.troves.data.local.database.CartDao
import com.troves.data.local.database.CartEntity
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.dto.CartItemDto
import com.troves.domain.entity.CartItem
import com.troves.domain.entity.Product
import com.troves.domain.repository.CartRepository
import com.troves.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CartRepositoryImpl(
    private val cartDao: CartDao,
    private val remoteDatasource: RemoteDatasource
) : CartRepository {

    override val cartItems: Flow<List<CartItem>> = cartDao.getAllCartItems().map { list ->
        list.map { it.toDomain() }
    }

    override suspend fun addToCart(product: Product, userId: String) {
        val existingItem = cartDao.getCartItem(product.id)
        if (existingItem != null) {
            val newQuantity = existingItem.quantity + 1
            cartDao.updateQuantity(product.id, newQuantity)
            remoteDatasource.addToCart(userId, product.toDto(newQuantity))
        } else {
            cartDao.insertCartItem(product.toEntity(quantity = 1))
            remoteDatasource.addToCart(userId, product.toDto(quantity = 1))
        }
    }

    override suspend fun removeFromCart(productId: Long, userId: String) {
        cartDao.deleteCartItem(productId)
        remoteDatasource.removeFromCart(userId, productId)
    }

    override suspend fun updateQuantity(productId: Long, quantity: Int, userId: String) {
        if (quantity > 0) {
            cartDao.updateQuantity(productId, quantity)
            // For Firestore we can just update the item by passing it again, but we need the product details.
            // Let's fetch from DAO to get product details, then push to remote.
            val existingItem = cartDao.getCartItem(productId)
            if (existingItem != null) {
                remoteDatasource.addToCart(userId, existingItem.toDto())
            }
        } else {
            cartDao.deleteCartItem(productId)
            remoteDatasource.removeFromCart(userId, productId)
        }
    }

    override suspend fun clearCart(userId: String) {
        cartDao.clearAll()
        remoteDatasource.clearCart(userId)
    }

    override suspend fun syncFromRemote(userId: String) {
        when (val result = remoteDatasource.getCart(userId)) {
            is Result.Success -> {
                result.value.forEach { dto ->
                    cartDao.insertCartItem(dto.toEntity())
                }
            }
            is Result.Error -> Unit
            Result.Loading -> Unit
        }
    }

    override suspend fun syncLocalOfflineCart(userId: String) {
        try {
            val allLocalCartItems = cartDao.getAllCartItems().first()
            allLocalCartItems.forEach { entity ->
                remoteDatasource.addToCart(userId, entity.toDto())
            }
        } catch (_: Exception) {
        }
    }

    override suspend fun clearLocal() {
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

    private fun Product.toDto(quantity: Int) = CartItemDto(
        id = id,
        title = title,
        vendor = vendor,
        price = price,
        imageUrl = imageUrl,
        status = status,
        quantity = quantity
    )

    private fun CartItemDto.toEntity() = CartEntity(
        id = id,
        title = title,
        vendor = vendor,
        price = price,
        imageUrl = imageUrl,
        status = status,
        quantity = quantity
    )

    private fun CartEntity.toDto() = CartItemDto(
        id = id,
        title = title,
        vendor = vendor,
        price = price,
        imageUrl = imageUrl,
        status = status,
        quantity = quantity
    )
}
