package com.troves.presintation.ui.allbrands

import com.troves.domain.entity.Brand

data class AllBrandsUiState(
    val isLoading: Boolean = true,
    val brands: List<Brand> = emptyList(),
    val errorMessage: String? = null,
) {
    val hasError: Boolean get() = errorMessage != null
}

sealed interface AllBrandsEffect {
    data class NavigateToProducts(
        val sourceType: String = "",
        val sourceId: String = "",
        val sourceName: String = "",
    ) : AllBrandsEffect
    data object NavigateBack : AllBrandsEffect
    data class ShowToast(val message: String) : AllBrandsEffect
}

sealed interface AllBrandsIntent {
    data object Load : AllBrandsIntent
    data object Retry : AllBrandsIntent
    data object OnBackClick : AllBrandsIntent
    data class BrandClicked(val brand: Brand) : AllBrandsIntent
}
