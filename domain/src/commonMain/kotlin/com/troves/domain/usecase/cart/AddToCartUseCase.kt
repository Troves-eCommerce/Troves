package com.troves.domain.usecase.cart

import com.troves.domain.entity.Product
import com.troves.domain.repository.CartRepository

class AddToCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(product: Product) = repository.addToCart(product)
}
