package com.troves.domain.repository

import com.troves.domain.entity.Product
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    fun getAllFavorites(): Flow<List<Product>>
    fun isFavorite(productId: String): Flow<Boolean>
    suspend fun addFavorite(product: Product)
    suspend fun deleteFavorite(productId: String)
}
