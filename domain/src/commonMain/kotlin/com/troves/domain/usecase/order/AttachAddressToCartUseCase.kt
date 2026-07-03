package com.troves.domain.usecase.order

import com.troves.domain.entity.Address
import com.troves.domain.repository.OrderRepository

class AttachAddressToCartUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(cartId: String, address: Address) =
        runCatching { orderRepository.attachAddressToCart(cartId, address) }
}
