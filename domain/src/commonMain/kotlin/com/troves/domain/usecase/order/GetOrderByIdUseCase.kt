package com.troves.domain.usecase.order

import com.troves.domain.entity.Order
import com.troves.domain.repository.OrderRepository

class GetOrderByIdUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(orderId: String): Order? =
        runCatching { orderRepository.getOrderById(orderId) }.getOrNull()
}
