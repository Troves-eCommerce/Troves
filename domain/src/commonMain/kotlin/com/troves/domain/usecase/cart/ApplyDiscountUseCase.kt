package com.troves.domain.usecase.cart

import com.troves.domain.entity.Cart
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.utils.NoConnectionException
import com.troves.domain.utils.connectivity.ConnectivityObserver
import com.troves.domain.utils.connectivity.ConnectivityStatus


sealed interface ApplyDiscountResult {
    data class Success(val cart: Cart) : ApplyDiscountResult
    data object RequiresLogin : ApplyDiscountResult
    data class Error(val throwable: Throwable) : ApplyDiscountResult
}

class ApplyDiscountUseCase(
    private val repository: TrovesRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val connectivityObserver: ConnectivityObserver,
) {
    suspend operator fun invoke(codes: List<String>): ApplyDiscountResult {
        if (connectivityObserver.currentStatus() != ConnectivityStatus.Available) {
            return ApplyDiscountResult.Error(NoConnectionException())
        }
        val userId = authenticationRepository.getCurrentUserId()
        if (userId == null || !authenticationRepository.isLoggedIn()) {
            return ApplyDiscountResult.RequiresLogin
        }
        return try {
            ApplyDiscountResult.Success(repository.applyDiscountCodes(codes))
        } catch (e: Exception) {
            ApplyDiscountResult.Error(e)
        }
    }
}
