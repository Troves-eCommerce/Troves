package com.troves.domain

/**
 * Single-responsibility use case: fetch all products.
 * ViewModels depend on this, not on the repository directly.
 */
class GetProductsUseCase(
    private val repository: TrovesRepository
) {
    suspend operator fun invoke(): Result<List<Product>> =
        repository.getAllProducts()
}
