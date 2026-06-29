package com.troves.domain.usecase.home

import com.troves.domain.Result
import com.troves.domain.entity.Product
import com.troves.domain.map
import com.troves.domain.usecase.shared.GetProductsUseCase

/**
 * "Just For You" has no dedicated endpoint — it's pure business logic over the
 * full catalogue: a randomised pick of up to 10 products as a stand-in for real
 * personalisation.
 */
class GetJustForYouProductsUseCase(
    private val getProducts: GetProductsUseCase,
) {
    suspend operator fun invoke(): Result<List<Product>> =
        getProducts().map { products ->
            products.shuffled().take(PRODUCT_LIMIT)
        }

    private companion object {
        const val PRODUCT_LIMIT = 10
    }
}
