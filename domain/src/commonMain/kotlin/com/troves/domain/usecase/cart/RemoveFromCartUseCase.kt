package com.troves.domain.usecase.cart

import com.troves.domain.repository.CartRepository

import com.troves.domain.repository.AuthenticationRepository

class RemoveFromCartUseCase(
    private val repository: CartRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(productId: Long): CartOperationResult {
        val userId = authenticationRepository.getCurrentUserId()
        if (userId == null || !authenticationRepository.isLoggedIn()) {
            return CartOperationResult.RequiresLogin
        }
        return try {
            repository.removeFromCart(productId, userId)
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e)
        }
    }
}
