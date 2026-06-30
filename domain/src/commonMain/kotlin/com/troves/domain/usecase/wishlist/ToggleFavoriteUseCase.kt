package com.troves.domain.usecase.wishlist

import com.troves.domain.entity.Product
import com.troves.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.first

class ToggleFavoriteUseCase(
    private val repository: WishlistRepository
) {
    suspend operator fun invoke(product: Product) {
        val isFavorite = repository.isFavorite(product.id.toString()).first()
        if (isFavorite) {
            repository.deleteFavorite(product.id.toString())
        } else {
            repository.addFavorite(product)
        }
    }
}
