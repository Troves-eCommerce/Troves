package com.troves.domain.home

import com.troves.domain.Product
import com.troves.domain.Result

/** Fetches the personalised "Just For You" product list. */
class GetJustForYouProductsUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<Product>> = repository.getJustForYouProducts()
}
