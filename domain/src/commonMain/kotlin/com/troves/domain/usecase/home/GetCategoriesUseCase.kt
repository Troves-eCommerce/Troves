package com.troves.domain.usecase.home

import com.troves.domain.entity.Category
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result
import com.troves.domain.utils.map

/** Fetches the categories shown in the Home "Categories" rail. */
class GetCategoriesUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(): Result<List<Category>> = repository.getCategories().map { categories ->
    categories.filter { !it.imageUrl.isNullOrEmpty() }.reversed()
    }
}
