package com.troves.domain.home

import com.troves.domain.Result

/** Fetches the categories shown in the Home "Categories" rail. */
class GetCategoriesUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<Category>> = repository.getCategories()
}
