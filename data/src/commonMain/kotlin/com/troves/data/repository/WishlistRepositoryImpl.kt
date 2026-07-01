package com.troves.data.repository

import com.troves.data.local.database.WishlistDao
import com.troves.data.local.database.WishlistEntity
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.dto.WishlistDto
import com.troves.domain.Result
import com.troves.domain.entity.Product
import com.troves.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class WishlistRepositoryImpl(
    private val wishlistDao: WishlistDao,
    private val remoteDatasource: RemoteDatasource,
) : WishlistRepository {

    override fun getAllFavorites(): Flow<List<Product>> =
        wishlistDao.getAllFavorites().map { list -> list.map { it.toDomain() } }

    override fun isFavorite(productId: String): Flow<Boolean> =
        wishlistDao.isFavorite(productId.toLongOrNull() ?: -1)

    override suspend fun addFavorite(product: Product, userId: String?) {
        if (userId == null) return // never persist locally without an authenticated user
        wishlistDao.addFavorite(product.toEntity())
        remoteDatasource.addToWishlist(userId, product.toDto())
        // if the remote call fails, local data still stands; consider a retry queue later
    }

    override suspend fun deleteFavorite(productId: String, userId: String?) {
        val id = productId.toLongOrNull() ?: return
        if (userId == null) return
        wishlistDao.deleteFavorite(id)
        remoteDatasource.removeFromWishlist(userId, id)
    }

    override suspend fun syncFromRemote(userId: String) {
        when (val result = remoteDatasource.getWishlist(userId)) {
            is Result.Success -> result.value.forEach { dto ->
                wishlistDao.addFavorite(dto.toEntity())
            }
            is Result.Error -> Unit // consider logging result.throwable here
            Result.Loading -> Unit
        }
    }

    override suspend fun syncLocalOfflineFavorites(userId: String) {
        try {
            val allLocalFavorites = wishlistDao.getAllFavorites().first()
            allLocalFavorites.forEach { entity ->
                remoteDatasource.addToWishlist(userId, entity.toDto())
            }
        } catch (e: Exception) {
            // silently fail - favorites are still stored locally
        }
    }

    override suspend fun clearLocal() {
        wishlistDao.clearAll()
    }

    private fun WishlistEntity.toDomain() = Product(
        id = id, title = title, vendor = vendor, price = price,
        imageUrl = imageUrl, status = status,
    )

    private fun Product.toEntity() = WishlistEntity(
        id = id, title = title, vendor = vendor, price = price,
        imageUrl = imageUrl, status = status,
    )

    private fun Product.toDto() = WishlistDto(
        id = id, title = title, vendor = vendor, price = price,
        imageUrl = imageUrl, status = status,
    )

    private fun WishlistDto.toEntity() = WishlistEntity(
        id = id, title = title, vendor = vendor, price = price,
        imageUrl = imageUrl, status = status,
    )

    // added: WishlistEntity -> WishlistDto, needed by syncLocalOfflineFavorites
    private fun WishlistEntity.toDto() = WishlistDto(
        id = id, title = title, vendor = vendor, price = price,
        imageUrl = imageUrl, status = status,
    )
}