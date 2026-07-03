package com.troves.domain.usecase.order

import com.troves.domain.repository.TrovesRepository


class ClearCartUseCase(
    private val cartRepository: TrovesRepository,
) {
    suspend operator fun invoke() {
        runCatching { cartRepository.clearCart() }
    }
}
