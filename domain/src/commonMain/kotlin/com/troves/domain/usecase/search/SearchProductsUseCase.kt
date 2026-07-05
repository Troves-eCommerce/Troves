package com.troves.domain.usecase.search

import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result

class SearchProductsUseCase(
    private val trovesRepository: TrovesRepository
){
    suspend operator fun invoke(params: ProductSearchParams): Result<List<Product>> {
        return trovesRepository.searchProducts(params)
    }
}