package com.troves.domain.usecase.cart

import com.troves.domain.entity.Cart
import com.troves.domain.repository.TrovesRepository
import kotlinx.coroutines.flow.Flow

class GetCartStreamUseCase(private val repository: TrovesRepository) {
    operator fun invoke(): Flow<Cart?> = repository.cart
}
