package com.troves.domain.usecase.auth

import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.WishlistRepository
import com.troves.domain.repository.CartRepository

class LogoutUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val wishlistRepository: WishlistRepository,
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke() {
        authenticationRepository.logout()
        wishlistRepository.clearLocal()
        cartRepository.clearLocal()
    }
}