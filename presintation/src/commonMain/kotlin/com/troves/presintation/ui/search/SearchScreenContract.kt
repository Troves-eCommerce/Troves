package com.troves.presintation.ui.search

import androidx.compose.runtime.Immutable
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category
import com.troves.domain.entity.Product

/**
 * Copyright (c) 2026 Wahid Ali Wahid Hussien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Author: Wahid Ali Wahid Hussien
 * Created: 30/06/2026
 */

@Immutable
data class SheetFilterOptions(
    val selectedCategories: Set<String> = emptySet(),
    val selectedBrands: Set<String> = emptySet(),
)

@Immutable
data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = true,
    val allProducts: List<Product> = emptyList(),
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val brands: List<Brand> = emptyList(),
    val selectedBrand: String = "",
    val sheetFilterOptions: SheetFilterOptions = SheetFilterOptions(),
    val recentSearches: Set<RecentSearchUi> = emptySet(),
    val errorMessage: String? = null,
    val showFilterSheet: Boolean = false,
    val favoriteProductIds: Set<String> = emptySet(),
) {
    val hasError get() = errorMessage != null
    val isEmpty get() = !isLoading && products.isEmpty() && !hasError
}

@Immutable
data class RecentSearchUi(
    val id: String,
    val query: String
)

sealed interface SearchIntent {
    data object Load : SearchIntent
    data object Retry : SearchIntent
    data object OnFilterClick : SearchIntent
    data object ClearFilters : SearchIntent
    data object OnBottomSheetDismiss : SearchIntent
    data class OnProductClick(val productId: String) : SearchIntent
    data class ApplyFilters(val sheetFilterOptions: SheetFilterOptions) : SearchIntent
    data class SearchQueryChange(val newQuery: String) : SearchIntent
    data class OnSearch(val query: String) : SearchIntent
    data class CategoriesChange(val newCategory: List<String>) : SearchIntent
    data class BrandsChange(val newBrands: List<String>) : SearchIntent
    data class BrandChange(val newBrand: String) : SearchIntent
    data class RemoveRecentSearch(val query: String) : SearchIntent
    data object ClearRecentSearches : SearchIntent
    data class ToggleFavorite(val productId: String) : SearchIntent
}

sealed interface SearchEffect {
    data object NavigateBack : SearchEffect
    data object HideKeyboard : SearchEffect
    data class NavigateToDetails(val productId: String) : SearchEffect
    data class ShowMessage(val message: String) : SearchEffect
}