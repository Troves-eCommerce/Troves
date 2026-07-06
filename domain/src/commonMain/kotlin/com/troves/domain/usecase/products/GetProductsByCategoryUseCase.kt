package com.troves.domain.usecase.products

import com.troves.domain.utils.Result
import com.troves.domain.entity.Product
import com.troves.domain.repository.TrovesRepository

class GetProductsByCategoryUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(categoryId: Long): Result<List<Product>> =
        repository.getProductsByCollection(collectionId = categoryId)
}
