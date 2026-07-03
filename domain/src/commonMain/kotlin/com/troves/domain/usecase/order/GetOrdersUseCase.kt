package com.troves.domain.usecase.order

import com.troves.domain.entity.Order
import com.troves.domain.repository.OrderRepository

class GetOrdersUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(): List<Order> =
        runCatching { orderRepository.getOrders() }.getOrDefault(emptyList())
}
