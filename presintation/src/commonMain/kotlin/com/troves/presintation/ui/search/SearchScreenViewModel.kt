package com.troves.presintation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.usecase.home.GetBrandsUseCase
import com.troves.domain.usecase.home.GetCategoriesUseCase
import com.troves.domain.usecase.search.SearchProductsUseCase
import com.troves.domain.usecase.shared.GetProductsUseCase
import com.troves.domain.usecase.wishlist.GetWishlistUseCase
import com.troves.domain.usecase.wishlist.ToggleFavoriteUseCase
import com.troves.domain.utils.Result
import com.troves.domain.utils.fold
import com.troves.domain.utils.getOrElse
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import com.troves.presintation.ui.search.SearchEffect.NavigateToDetails
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.firstOrNull
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
    private val searchProductsUseCase: SearchProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getWishlistUseCase: GetWishlistUseCase,
) : ViewModel(),
    StateHolder<SearchUiState> by DefaultStateHolder(SearchUiState()),
    EffectPublisher<SearchEffect> by DefaultEffectPublisher() {

    private val searchQueryFlow = MutableStateFlow("")

    init {
        searchQueryFlow
            .filterNot { it.isBlank() }
            .debounce(500.milliseconds)
            .distinctUntilChanged()
            .onEach { q ->
                runSearch(q)
                updateState {
                    val searches = recentSearches.toMutableList()
                    if (!searches.contains(q)) {
                        searches.add(0, q)
                    }
                    copy(recentSearches = searches.take(10))
                }
            }
            .launchIn(viewModelScope)

        getWishlistUseCase()
            .onEach { wishlist ->
                updateState { copy(favoriteProductIds = wishlist.map { it.id.toString() }.toSet()) }
            }
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
                viewModelScope.launch { runSearch(state.value.query) }
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
                viewModelScope.launch { runSearch(state.value.query) }
            }

            is SearchIntent.SearchQueryChange -> {
                if (intent.newQuery.isBlank()) {
                    viewModelScope.launch {
                        updateState { copy(query = intent.newQuery, products = emptyList()) }
                    }
                } else {
                    updateState { copy(query = intent.newQuery) }
                }
                searchQueryFlow.value = intent.newQuery
            }

            SearchIntent.ClearFilters -> {
                viewModelScope.launch {
                    updateState { copy(sheetFilterOptions = SheetFilterOptions()) }
                    runSearch(state.value.query)
                }
            }

            is SearchIntent.OnProductClick -> sendEffect(NavigateToDetails(intent.productId))

            SearchIntent.OnBottomSheetDismiss -> updateState { copy(showFilterSheet = false) }

            is SearchIntent.OnSearch -> {
                updateState { copy(query = intent.query) }
                searchQueryFlow.value = intent.query
                viewModelScope.launch { 
                    runSearch(query = intent.query) 
                    updateState {
                        val searches = recentSearches.toMutableList()
                        if (intent.query.isNotBlank() && !searches.contains(intent.query)) {
                            searches.add(0, intent.query)
                        }
                        copy(recentSearches = searches.take(10))
                    }
                }
            }

            is SearchIntent.BrandChange -> {
                viewModelScope.launch {
                    val newBrand =
                        if (state.value.selectedBrand == intent.newBrand) "" else intent.newBrand
                    updateState { copy(selectedBrand = newBrand) }
                    runSearch(state.value.query)
                }
            }

            is SearchIntent.ApplyFilters -> {
                viewModelScope.launch {
                    updateState {
                        copy(
                            sheetFilterOptions = intent.sheetFilterOptions,
                            showFilterSheet = false
                        )
                    }
                    runSearch(state.value.query)
                }
            }

            is SearchIntent.RemoveRecentSearch -> {
                updateState {
                    copy(recentSearches = recentSearches.filterNot { it == intent.query })
                }
            }

            SearchIntent.ClearRecentSearches -> {
                updateState { copy(recentSearches = emptyList()) }
            }

            is SearchIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    val product = state.value.products.find { it.id.toString() == intent.productId }
                        ?: state.value.allProducts.find { it.id.toString() == intent.productId }
                        ?: return@launch

                    when (val result = toggleFavoriteUseCase(product)) {
                        is com.troves.domain.usecase.wishlist.ToggleFavoriteResult.RequiresLogin -> {
                            sendEffect(SearchEffect.ShowMessage("Please login to add to wishlist"))
                        }

                        is com.troves.domain.usecase.wishlist.ToggleFavoriteResult.Error -> {
                            sendEffect(
                                SearchEffect.ShowMessage(
                                    result.throwable.message ?: "An error occurred"
                                )
                            )
                        }

                        com.troves.domain.usecase.wishlist.ToggleFavoriteResult.Added -> {
                            sendEffect(SearchEffect.ShowMessage("Added to wishlist"))
                        }

                        com.troves.domain.usecase.wishlist.ToggleFavoriteResult.Removed -> {
                            sendEffect(SearchEffect.ShowMessage("Removed from wishlist"))
                        }
                    }
                }
            }
        }
    }

    private fun toggleAll(current: Set<String>, ids: List<String>): Set<String> =
        ids.fold(current) { acc, id -> if (id in acc) acc - id else acc + id }

    private suspend fun runSearch(query: String) {
        updateState { copy(isLoading = true) }

        val categoryNames = state.value.categories
            .filter { it.id.toString() in state.value.sheetFilterOptions.selectedCategories }
            .map { it.name }

        val sheetBrandNames = state.value.brands
            .filter { it.id.toString() in state.value.sheetFilterOptions.selectedBrands }
            .map { it.name }

        val brandNames = sheetBrandNames.toMutableSet()
        if (state.value.selectedBrand.isNotBlank()) {
            brandNames.add(state.value.selectedBrand)
        }

        val params = ProductSearchParams(
            query = query.ifBlank { null },
            vendors = brandNames.toList().ifEmpty { null },
            productTypes = categoryNames.ifEmpty { null },
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
            updateState { copy(isInitializing = true, errorMessage = null) }
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
                    isInitializing = false,
                    allProducts = loadedProducts,
                    brands = brandsResult.getOrElse { emptyList() },
                    categories = categoryResult.getOrElse { emptyList() },
                    errorMessage = hasError?.message,
                )
            }
        }
    }
}