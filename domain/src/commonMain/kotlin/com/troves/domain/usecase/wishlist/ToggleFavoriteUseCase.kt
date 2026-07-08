package com.troves.domain.usecase.wishlist

import com.troves.domain.entity.Product
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.WishlistRepository
import com.troves.domain.utils.NoConnectionException
import com.troves.domain.utils.connectivity.ConnectivityObserver
import com.troves.domain.utils.connectivity.ConnectivityStatus
import kotlinx.coroutines.flow.first

class ToggleFavoriteUseCase(
    private val wishlistRepository: WishlistRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val connectivityObserver: ConnectivityObserver,
) {
    suspend operator fun invoke(product: Product): ToggleFavoriteResult {

        // Wishlist is a Shopify-backed mutation — block it entirely while offline
        // so every caller (home, search, products, details, wishlist, AI) is covered.
        if (connectivityObserver.currentStatus() != ConnectivityStatus.Available) {
            return ToggleFavoriteResult.Error(NoConnectionException())
        }

        if (!authenticationRepository.isLoggedIn()) {
            return ToggleFavoriteResult.RequiresLogin
        }
        val userId = authenticationRepository.getCurrentUserId()
            ?: return ToggleFavoriteResult.RequiresLogin
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