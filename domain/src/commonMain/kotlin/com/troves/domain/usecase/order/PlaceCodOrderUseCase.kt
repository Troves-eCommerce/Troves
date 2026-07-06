package com.troves.domain.usecase.order

import com.troves.domain.entity.Address
import com.troves.domain.entity.Cart
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.TrovesRepository

sealed interface PlaceOrderResult {
    data class Success(val orderName: String) : PlaceOrderResult
    data object RequiresLogin : PlaceOrderResult
    data object NoAddress : PlaceOrderResult
    data object EmptyCart : PlaceOrderResult
    data class Error(val throwable: Throwable) : PlaceOrderResult
}


class PlaceCodOrderUseCase(
    private val repository: TrovesRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(cart: Cart, address: Address?): PlaceOrderResult {
        if (!authenticationRepository.isLoggedIn()) return PlaceOrderResult.RequiresLogin
        if (cart.lines.isEmpty()) return PlaceOrderResult.EmptyCart
        if (address == null || !address.isDeliverable) return PlaceOrderResult.NoAddress
        return try {
            val orderName = repository.placeCodOrder(cart, address)
            repository.clearCart()
            PlaceOrderResult.Success(orderName)
        } catch (e: Exception) {
            PlaceOrderResult.Error(e)
        }
    }
}
