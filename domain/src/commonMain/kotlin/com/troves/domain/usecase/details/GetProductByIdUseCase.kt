package com.troves.domain.usecase.details

import com.troves.domain.repository.TrovesRepository

data class GetProductByIdUseCase(
    private val repository: TrovesRepository
) {

     suspend operator fun invoke(productId: String) = repository.getProductById(productId = productId)

}