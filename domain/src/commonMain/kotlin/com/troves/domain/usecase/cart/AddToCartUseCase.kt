package com.troves.domain.usecase.cart

import com.troves.domain.entity.Product
import com.troves.domain.repository.CartRepository
import com.troves.domain.repository.AuthenticationRepository

class AddToCartUseCase(
    private val repository: CartRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(product: Product): CartOperationResult {
        val userId = authenticationRepository.getCurrentUserId()
        if (userId == null || !authenticationRepository.isLoggedIn()) {
            return CartOperationResult.RequiresLogin
        }
        return try {
            repository.addToCart(product, userId)
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e)
        }
    }
}
