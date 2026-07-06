package com.troves.domain.usecase.products

import com.troves.domain.utils.Result
import com.troves.domain.entity.Product
import com.troves.domain.repository.TrovesRepository

class GetProductsByBrandUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(brandName: String): Result<List<Product>> =
        repository.getProductsByVendor(vendorName = brandName)
}
