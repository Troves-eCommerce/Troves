package com.troves.domain.usecase.order

import com.troves.domain.repository.CartRepository


class ClearCartUseCase(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke() {
        runCatching { cartRepository.clearCart() }
    }
}
