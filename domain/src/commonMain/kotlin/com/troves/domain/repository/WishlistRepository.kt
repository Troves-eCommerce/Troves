package com.troves.domain.repository

import com.troves.domain.entity.Product
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    fun getAllFavorites(): Flow<List<Product>>
    fun isFavorite(productId: String): Flow<Boolean>
    suspend fun addFavorite(product: Product, userId: String)
    suspend fun deleteFavorite(productId: String, userId: String)
    suspend fun syncFromRemote(userId: String)
    suspend fun syncLocalOfflineFavorites(userId: String)
    suspend fun clearLocal()
}
