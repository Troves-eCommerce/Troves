package com.troves.domain.usecase.cart

import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.TrovesRepository


class RefreshCartUseCase(
    private val repository: TrovesRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke() {
        if (!authenticationRepository.isLoggedIn()) return
        runCatching { repository.refreshCart() }
    }
}
