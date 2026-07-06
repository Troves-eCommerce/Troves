package com.troves.domain.usecase.cart

import com.troves.domain.repository.TrovesRepository
import com.troves.domain.repository.AuthenticationRepository

class UpdateCartQuantityUseCase(
    private val repository: TrovesRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(lineId: String, quantity: Int): CartOperationResult {
        val userId = authenticationRepository.getCurrentUserId()
        if (userId == null || !authenticationRepository.isLoggedIn()) {
            return CartOperationResult.RequiresLogin
        }
        return try {
            repository.updateQuantity(lineId, quantity)
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e)
        }
    }
}
