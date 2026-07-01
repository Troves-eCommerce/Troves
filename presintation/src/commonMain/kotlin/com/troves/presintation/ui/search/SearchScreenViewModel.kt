package com.troves.presintation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.usecase.home.GetBrandsUseCase
import com.troves.domain.usecase.home.GetCategoriesUseCase
import com.troves.domain.usecase.products.FilterProductsUseCase
import com.troves.domain.usecase.search.FilterProductsByQueryUseCase
import com.troves.domain.usecase.search.SearchProductsUseCase
import com.troves.domain.usecase.shared.GetProductsUseCase
import com.troves.domain.utils.Result
import com.troves.domain.utils.fold
import com.troves.domain.utils.getOrElse
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import com.troves.presintation.ui.search.SearchEffect.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

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

@OptIn(FlowPreview::class)
class SearchScreenViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getBrandsUseCase: GetBrandsUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val filterProductsByQueryUseCase: FilterProductsByQueryUseCase,
    private val filterProductsUseCase: FilterProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
) : ViewModel(),
    StateHolder<SearchUiState> by DefaultStateHolder(SearchUiState()),
    EffectPublisher<SearchEffect> by DefaultEffectPublisher() {

    private val searchQueryFlow = MutableStateFlow("")

    init {
        searchQueryFlow
            .debounce(350.milliseconds)
            .distinctUntilChanged()
            .onEach { q -> runSearch(q) }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            SearchIntent.Load, SearchIntent.Retry -> load()

            SearchIntent.OnFilterClick -> updateState { copy(showFilterSheet = true) }

            is SearchIntent.BrandsChange -> {
                updateState {
                    copy(
                        sheetFilterOptions = sheetFilterOptions.copy(
                            selectedBrands = toggleAll(
                                sheetFilterOptions.selectedBrands,
                                intent.newBrands
                            )
                        )
                    )
                }
            }

            is SearchIntent.CategoriesChange -> {
                updateState {
                    copy(
                        sheetFilterOptions = sheetFilterOptions.copy(
                            selectedCategories = toggleAll(
                                sheetFilterOptions.selectedCategories,
                                intent.newCategory
                            )
                        )
                    )
                }
            }

            is SearchIntent.SearchQueryChange -> {
                updateState { copy(query = intent.newQuery) }
                searchQueryFlow.value = intent.newQuery
            }

            SearchIntent.ClearFilters -> {
                updateState {
                    copy(sheetFilterOptions = SheetFilterOptions(), products = allProducts)
                }
            }

            is SearchIntent.OnProductClick -> sendEffect(NavigateToDetails(intent.productId))

            SearchIntent.OnBottomSheetDismiss -> updateState { copy(showFilterSheet = false) }

            is SearchIntent.OnSearch -> {
                viewModelScope.launch { runSearch(query = "", vendor = intent.query) }
            }

            is SearchIntent.BrandChange -> {
                updateState { copy(selectedBrand = intent.newBrand) }
                onIntent(SearchIntent.OnSearch(intent.newBrand))
            }

            is SearchIntent.ApplyFilters -> {
                viewModelScope.launch {
                    updateState {
                        copy(isLoading = true, sheetFilterOptions = intent.sheetFilterOptions, showFilterSheet = false)
                    }
                    val categoryNames = state.value.categories
                        .filter { it.id.toString() in intent.sheetFilterOptions.selectedCategories }
                        .map { it.name }
                        .toSet()
                    val brandNames = state.value.brands
                        .filter { it.id.toString() in intent.sheetFilterOptions.selectedBrands }
                        .map { it.name }
                        .toSet()

                    val filtered = filterProductsUseCase(
                        state.value.allProducts,
                        brandIds = brandNames,
                        categoryNames = categoryNames,
                    )
                    updateState { copy(isLoading = false, products = filtered) }
                }
            }
        }
    }

    private fun toggleAll(current: Set<String>, ids: List<String>): Set<String> =
        ids.fold(current) { acc, id -> if (id in acc) acc - id else acc + id }

    private suspend fun runSearch(query: String, vendor: String? = null) {
        updateState { copy(isLoading = true) }

        val params = ProductSearchParams(
            query = query.ifBlank { null },
            vendor = vendor,
            productType = state.value.sheetFilterOptions.selectedCategories.firstOrNull(),
        )

        searchProductsUseCase(params = params).fold(
            onSuccess = { products ->
                updateState { copy(isLoading = false, products = products, errorMessage = null) }
            },
            onError = { throwable ->
                updateState { copy(isLoading = false, errorMessage = throwable.message) }
            },
            onLoading = { /* searchProductsUseCase resolves directly to Success/Error; unreachable here */ },
        )
    }

    private fun load() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            val categories = async { getCategoriesUseCase() }
            val brands = async { getBrandsUseCase() }
            val products = async { getProductsUseCase() }

            val categoryResult = categories.await()
            val brandsResult = brands.await()
            val productsResult = products.await()

            val hasError = listOf(categoryResult, brandsResult, productsResult)
                .firstNotNullOfOrNull { (it as? Result.Error)?.throwable }

            val loadedProducts = productsResult.getOrElse { emptyList() }

            updateState {
                copy(
                    isLoading = false,
                    allProducts = loadedProducts,
                    products = loadedProducts,
                    brands = brandsResult.getOrElse { emptyList() },
                    categories = categoryResult.getOrElse { emptyList() },
                    errorMessage = hasError?.message,
                )
            }
        }
    }
}