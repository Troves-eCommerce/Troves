package com.troves.domain.usecase.products

import com.troves.domain.Result
import com.troves.domain.entity.Product
import com.troves.domain.repository.TrovesRepository

class GetProductsByBrandUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(brandName: String): Result<List<Product>> =
        when (val result = repository.getAllProducts()) {
            is Result.Success -> Result.Success(
                result.value.filter { it.vendor.equals(brandName, ignoreCase = true) },
            )
            is Result.Error -> result
            is Result.Loading -> result
        }
}
