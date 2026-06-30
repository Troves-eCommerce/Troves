package com.troves.domain.usecase.cart

import com.troves.domain.repository.CartRepository

class UpdateCartQuantityUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(productId: Long, quantity: Int) =
        repository.updateQuantity(productId, quantity)
}
