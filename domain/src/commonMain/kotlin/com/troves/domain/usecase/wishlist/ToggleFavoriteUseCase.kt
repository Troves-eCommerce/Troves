package com.troves.domain.usecase.wishlist

import com.troves.domain.AuthenticationRepository
import com.troves.domain.entity.Product
import com.troves.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.first

class ToggleFavoriteUseCase(
    private val wishlistRepository: WishlistRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(product: Product): ToggleFavoriteResult {
        val userId = authenticationRepository.getCurrentUserId()

        return try {
            val isFav = wishlistRepository.isFavorite(product.id.toString()).first()
            if (isFav) {
                wishlistRepository.deleteFavorite(product.id.toString(), userId)
                ToggleFavoriteResult.Removed
            } else {
                wishlistRepository.addFavorite(product, userId)
                ToggleFavoriteResult.Added
            }
        } catch (e: Exception) {
            ToggleFavoriteResult.Error(e)
        }
    }
}