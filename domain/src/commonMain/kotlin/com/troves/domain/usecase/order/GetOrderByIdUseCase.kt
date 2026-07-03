package com.troves.domain.usecase.order

import com.troves.domain.entity.Order
import com.troves.domain.repository.TrovesRepository

class GetOrderByIdUseCase(
    private val orderRepository: TrovesRepository,
) {
    suspend operator fun invoke(orderId: String): Order? =
        runCatching { orderRepository.getOrderById(orderId) }.getOrNull()
}
