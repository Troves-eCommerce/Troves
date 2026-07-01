package com.troves.domain.usecase.shared

import com.troves.domain.entity.Product
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result

class GetProductsUseCase(
    private val repository: TrovesRepository
) {
    suspend operator fun invoke(): Result<List<Product>> =
        repository.getAllProducts()
}