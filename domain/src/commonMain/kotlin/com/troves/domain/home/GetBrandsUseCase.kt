package com.troves.domain.home

import com.troves.domain.Result

/** Fetches the brands shown in the Home "Brands" rail. */
class GetBrandsUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<Brand>> = repository.getBrands()
}
