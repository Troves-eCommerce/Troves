package com.troves.domain.usecase.cart

import com.troves.domain.repository.CartRepository
import com.troves.domain.repository.AuthenticationRepository


class RefreshCartUseCase(
    private val repository: CartRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke() {
        if (!authenticationRepository.isLoggedIn()) return
        runCatching { repository.refreshCart() }
    }
}
