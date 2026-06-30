package com.troves.domain.usecase.cart

import com.troves.domain.entity.CartItem
import com.troves.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class GetCartStreamUseCase(private val repository: CartRepository) {
    operator fun invoke(): Flow<List<CartItem>> = repository.cartItems
}
