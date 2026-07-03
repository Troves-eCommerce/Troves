package com.troves.domain.usecase.order

import com.troves.domain.entity.Address
import com.troves.domain.repository.TrovesRepository

class AttachAddressToCartUseCase(
    private val orderRepository: TrovesRepository,
) {
    suspend operator fun invoke(cartId: String, address: Address) =
        runCatching { orderRepository.attachAddressToCart(cartId, address) }
}
