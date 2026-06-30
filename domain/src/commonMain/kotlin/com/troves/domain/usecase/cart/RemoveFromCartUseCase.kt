package com.troves.domain.usecase.cart

import com.troves.domain.repository.CartRepository

class RemoveFromCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(productId: Long) = repository.removeFromCart(productId)
}
