package com.troves.domain.usecase.cart

import com.troves.domain.repository.TrovesRepository
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.utils.NoConnectionException
import com.troves.domain.utils.connectivity.ConnectivityObserver
import com.troves.domain.utils.connectivity.ConnectivityStatus


class AddToCartUseCase(
    private val repository: TrovesRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val connectivityObserver: ConnectivityObserver,
) {
    suspend operator fun invoke(variantId: String, quantity: Int = 1): CartOperationResult {
        if (connectivityObserver.currentStatus() != ConnectivityStatus.Available) {
            return CartOperationResult.Error(NoConnectionException())
        }
        val userId = authenticationRepository.getCurrentUserId()
        if (userId == null || !authenticationRepository.isLoggedIn()) {
            return CartOperationResult.RequiresLogin
        }
        return try {
            repository.addToCart(variantId, quantity)
            CartOperationResult.Success
        } catch (e: Exception) {
            CartOperationResult.Error(e)
        }
    }
}
