package com.troves.domain.usecase.home

import com.troves.domain.entity.Brand
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result

/** Fetches the brands shown in the Home "Brands" rail. */
class GetBrandsUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(): Result<List<Brand>> = repository.getBrands()
}
