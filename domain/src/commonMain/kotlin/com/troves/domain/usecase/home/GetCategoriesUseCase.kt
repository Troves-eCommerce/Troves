package com.troves.domain.home

import com.troves.domain.Result
import com.troves.domain.entity.Category

class GetCategoriesUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<Category>> = repository.getCategories()
}
