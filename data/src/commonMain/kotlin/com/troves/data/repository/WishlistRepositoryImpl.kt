package com.troves.data.repository

import com.troves.data.local.database.WishlistDao
import com.troves.data.local.database.WishlistEntity
import com.troves.domain.entity.Product
import com.troves.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WishlistRepositoryImpl(
    private val wishlistDao: WishlistDao
) : WishlistRepository {

    override fun getAllFavorites(): Flow<List<Product>> =
        wishlistDao.getAllFavorites().map { list -> list.map { it.toDomain() } }

    override fun isFavorite(productId: String): Flow<Boolean> =
        wishlistDao.isFavorite(productId.toLongOrNull() ?: -1)

    override suspend fun addFavorite(product: Product) {
        wishlistDao.addFavorite(product.toEntity())
    }

    override suspend fun deleteFavorite(productId: String) {
        productId.toLongOrNull()?.let { wishlistDao.deleteFavorite(it) }
    }

    private fun WishlistEntity.toDomain(): Product {
        return Product(
            id = id,
            title = title,
            vendor = vendor,
            price = price,
            imageUrl = imageUrl,
            status = status,
        )
    }

    private fun Product.toEntity(): WishlistEntity {
        return WishlistEntity(
            id = id,
            title = title,
            vendor = vendor,
            price = price,
            imageUrl = imageUrl,
            status = status,
        )
    }
}
