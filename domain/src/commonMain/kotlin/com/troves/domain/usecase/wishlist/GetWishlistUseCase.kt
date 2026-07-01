package com.troves.domain.usecase.wishlist

import com.troves.domain.entity.Product
import com.troves.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow

class GetWishlistUseCase(
    private val repository: WishlistRepository
) {
    operator fun invoke(): Flow<List<Product>> = repository.getAllFavorites()
}
