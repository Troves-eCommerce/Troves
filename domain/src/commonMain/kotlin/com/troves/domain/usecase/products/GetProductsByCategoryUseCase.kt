package com.troves.domain.usecase.products

import com.troves.domain.Result
import com.troves.domain.entity.Product
import com.troves.domain.repository.TrovesRepository

class GetProductsByCategoryUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(categoryName: String): Result<List<Product>> =
        when (val result = repository.getAllProducts()) {
            is Result.Success -> Result.Success(
                result.value.filter { it.title.contains(categoryName, ignoreCase = true) },
            )
            is Result.Error -> result
            is Result.Loading -> result
        }
}
