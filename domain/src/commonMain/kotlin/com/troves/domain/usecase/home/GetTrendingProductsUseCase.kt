package com.troves.domain.usecase.home

import com.troves.domain.entity.Product
import com.troves.domain.usecase.shared.GetProductsUseCase
import com.troves.domain.utils.Result
import com.troves.domain.utils.map

/**
 * "Trending Now" has no dedicated endpoint — it's pure business logic over the
 * full catalogue: keep only active products, then take up to 10.
 */
class GetTrendingProductsUseCase(
    private val getProducts: GetProductsUseCase,
) {
    suspend operator fun invoke(): Result<List<Product>> =
        getProducts().map { products ->
            products
                .filter { it.status.equals("active", ignoreCase = true) }
                .take(PRODUCT_LIMIT)
        }

    private companion object {
        const val PRODUCT_LIMIT = 10
    }
}
