package com.troves.domain.usecase.wishlist

import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.WishlistRepository

class SyncWishlistUseCase(
    private val wishlistRepository: WishlistRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(forceRefresh: Boolean = false) {
        val userId = authenticationRepository.getCurrentUserId() ?: return
        
        if (forceRefresh) {
            // If force refresh, we only pull from remote to ensure local matches remote (including deletions)
            wishlistRepository.syncFromRemote(userId)
        } else {
            // Normal sync: first push local changes to remote, then pull
            wishlistRepository.syncLocalOfflineFavorites(userId)
            wishlistRepository.syncFromRemote(userId)
        }
    }
}