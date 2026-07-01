package com.troves.domain.usecase.auth

import com.troves.domain.AuthenticationRepository
import com.troves.domain.repository.WishlistRepository

class LogoutUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val wishlistRepository: WishlistRepository,
) {
    suspend operator fun invoke() {
        authenticationRepository.logout()
        wishlistRepository.clearLocal()
    }
}