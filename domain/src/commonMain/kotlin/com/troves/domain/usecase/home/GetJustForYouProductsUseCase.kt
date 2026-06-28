package com.troves.domain.home

import com.troves.domain.entity.Product
import com.troves.domain.Result

class GetJustForYouProductsUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<Product>> = repository.getJustForYouProducts()
}
