package com.troves.domain.usecase.order

import com.troves.domain.entity.Order
import com.troves.domain.repository.TrovesRepository

class GetOrdersUseCase(
    private val orderRepository: TrovesRepository,
) {
    suspend operator fun invoke(): List<Order> =
        runCatching { orderRepository.getOrders() }.getOrDefault(emptyList())
}
