package com.troves.domain.home

import com.troves.domain.Result
import com.troves.domain.entity.Brand

class GetBrandsUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<Brand>> = repository.getBrands()
}
