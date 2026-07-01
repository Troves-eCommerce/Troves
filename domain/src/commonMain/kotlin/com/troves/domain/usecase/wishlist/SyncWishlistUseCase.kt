package com.troves.domain.usecase.wishlist

import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.WishlistRepository

class SyncWishlistUseCase(
    private val wishlistRepository: WishlistRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke() {
        val userId = authenticationRepository.getCurrentUserId() ?: return
        wishlistRepository.syncFromRemote(userId)
        wishlistRepository.syncLocalOfflineFavorites(userId)
    }
}