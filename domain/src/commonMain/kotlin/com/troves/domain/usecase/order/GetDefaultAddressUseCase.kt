package com.troves.domain.usecase.order

import com.troves.domain.entity.Address
import com.troves.domain.repository.OrderRepository

class GetDefaultAddressUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(): Address? = runCatching { orderRepository.getDefaultAddress() }.getOrNull()
}
