package com.troves.domain.usecase.order

import com.troves.domain.entity.Address
import com.troves.domain.repository.TrovesRepository

class GetDefaultAddressUseCase(
    private val orderRepository: TrovesRepository,
) {
    suspend operator fun invoke(): Address? = runCatching { orderRepository.getDefaultAddress() }.getOrNull()
}
