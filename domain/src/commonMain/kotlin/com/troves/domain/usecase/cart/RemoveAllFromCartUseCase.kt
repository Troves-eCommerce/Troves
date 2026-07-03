package com.troves.domain.usecase.cart

import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.TrovesRepository


class RemoveAllFromCartUseCase(
    private val repository: TrovesRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(): CartOperationResult {
        val userId = authenticationRepository.getCurrentUserId()
        if (userId == null || !authenticationRepository.isLoggedIn()) {
            return CartOperationResult.RequiresLogin
        }
        return try {
            repository.removeAllItems()
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e)
        }
    }
}
