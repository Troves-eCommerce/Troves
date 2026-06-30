package com.troves.presintation.ui.products

import com.troves.domain.entity.Product
import com.troves.presintation.ui.components.FilterOption
import com.troves.presintation.ui.components.SortOption

/**
 * UI state for the products listing screen.
 *
 * Selections come in two flavours: the **applied** sets (what the list is
 * actually filtered/sorted by) and the **draft** sets (what the user is editing
 * inside an open bottom sheet, before pressing Apply). Keeping them separate lets
 * the user cancel a sheet without mutating the visible list.
 */
data class ProductsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val allProducts: List<Product> = emptyList(),
    val displayedProducts: List<Product> = emptyList(),

    // Filter option sources (already localized from the data layer).
    val categoryOptions: List<FilterOption> = emptyList(),
    val subCategoryOptions: List<FilterOption> = emptyList(),
    val brandOptions: List<FilterOption> = emptyList(),

    // Applied filter selections.
    val selectedCategoryIds: Set<String> = emptySet(),
    val selectedSubCategoryIds: Set<String> = emptySet(),
    val selectedBrandIds: Set<String> = emptySet(),
    val selectedSort: SortOption = SortOption.DEFAULT,

    // Draft selections being edited inside an open sheet.
    val draftCategoryIds: Set<String> = emptySet(),
    val draftSubCategoryIds: Set<String> = emptySet(),
    val draftBrandIds: Set<String> = emptySet(),
    val draftSort: SortOption = SortOption.DEFAULT,

    // Sheet visibility.
    val showFilterSheet: Boolean = false,
    val showSortSheet: Boolean = false,
) {
    val hasError: Boolean get() = errorMessage != null

    val hasActiveFilters: Boolean
        get() = selectedCategoryIds.isNotEmpty() ||
            selectedSubCategoryIds.isNotEmpty() ||
            selectedBrandIds.isNotEmpty()

    val isSorted: Boolean get() = selectedSort != SortOption.DEFAULT
}

sealed interface ProductsEffect {
    data class NavigateToProduct(val productId: String) : ProductsEffect
    data object NavigateBack : ProductsEffect
    data class ShowToast(val message: String) : ProductsEffect
}

sealed interface ProductsIntent {
    data object Load : ProductsIntent
    data object Retry : ProductsIntent
    data object OnBackClick : ProductsIntent

    data object OpenFilter : ProductsIntent
    data object OpenSort : ProductsIntent
    data object DismissSheet : ProductsIntent

    data class ToggleCategory(val id: String) : ProductsIntent
    data class ToggleSubCategory(val id: String) : ProductsIntent
    data class ToggleBrand(val id: String) : ProductsIntent
    data object ApplyFilter : ProductsIntent
    data object ResetFilter : ProductsIntent

    data class SelectSort(val option: SortOption) : ProductsIntent
    data object ApplySort : ProductsIntent

    data class ProductClicked(val product: Product) : ProductsIntent
}
