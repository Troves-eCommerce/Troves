package com.troves.domain.usecase.wishlist

import com.troves.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow

class IsProductFavoritedUseCase(
    private val repository: WishlistRepository
) {
   operator fun invoke(productId: String): Flow<Boolean> = repository.isFavorite(productId)
}
