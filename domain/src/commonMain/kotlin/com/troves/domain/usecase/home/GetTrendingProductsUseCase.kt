package com.troves.domain.home

import com.troves.domain.entity.Product
import com.troves.domain.Result

/** Fetches the "Trending Now" product list. */
class GetTrendingProductsUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<Product>> = repository.getTrendingProducts()
}
