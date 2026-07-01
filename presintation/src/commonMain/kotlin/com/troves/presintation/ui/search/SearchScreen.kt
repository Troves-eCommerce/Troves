package com.troves.presintation.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.components.FilterBottomSheet
import com.troves.presintation.ui.components.FilterOption
import com.troves.presintation.ui.search.component.BrandRow
import com.troves.presintation.ui.search.component.EmptySearchResult
import com.troves.presintation.ui.search.component.ErrorView
import com.troves.presintation.ui.search.component.ProductGrid
import com.troves.presintation.ui.search.component.ProductLoading
import com.troves.presintation.ui.search.component.RecentSearchSection
import com.troves.presintation.ui.search.component.SearchBarSection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.collections.buildList

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (productId: String) -> Unit,
    state: SearchUiState,
    effect: Flow<SearchEffect>,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier
) {

    val coroutineScope = rememberCoroutineScope()
    val snakBarState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        onIntent(SearchIntent.Load)
    }


    ObserveEffect(effect) { effect ->
        when (effect) {

            SearchEffect.NavigateBack -> {
                onNavigateBack()
            }

            is SearchEffect.ShowMessage -> {
                coroutineScope.launch {
                    snakBarState.showSnackbar(message = effect.message)
                }
            }

            SearchEffect.HideKeyboard -> {

            }

            is SearchEffect.NavigateToDetails -> {
                onNavigateToDetails(effect.productId)
            }
        }
    }

    Scaffold(
        modifier = modifier.background(
            color = Theme.colors.backGround
        ).fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            SearchBarSection(
                query = state.query ?: "",
                onQueryChange = { onIntent(SearchIntent.SearchQueryChange(it)) },
                onSearch = {
                    onIntent(SearchIntent.OnSearch(it))
                },
                onFilterClick = { onIntent(SearchIntent.OnFilterClick) },
                searchSuggestions = buildList {
                    state.categories.map { it.name }.forEach { add(it) }
                    state.brands.map { it.name }.forEach { add(it) }
                },
            )
        },
        containerColor = Theme.colors.backGround,
    ) {
        Box(
            modifier = modifier.background(
                color = Theme.colors.backGround
            ).fillMaxSize()
        ) {

            Column {
                RecentSearchSection(
                    recentSearches = state.recentSearches.toList(),
                    onSearchClick = {},
                    onRemoveClick = {}
                )

                BrandRow(
                    brands = state.brands.toList(),
                    selectedBrand = state.selectedBrand,
                    modifier = Modifier.fillMaxWidth(),
                    onBrandSelected = { onIntent(SearchIntent.BrandChange(it.name)) }
                )
                when {
                    state.isLoading -> {
                        ProductLoading(
                            modifier = Modifier.background(
                                color = Theme.colors.backGround
                            )
                        )
                    }

                    state.hasError -> {
                        ErrorView(
                            message = state.errorMessage.orEmpty()
                        )
                    }

                    state.isEmpty -> {
                        EmptySearchResult(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                        )
                    }

                    else -> {
                        ProductGrid(
                            products = state.products,
                            onProductClick = {
                                onIntent(SearchIntent.OnProductClick(it.id.toString()))
                            }
                        )

                    }
                }
            }
            if (state.showFilterSheet) {
                FilterBottomSheet(
                    categories = state.categories.map { FilterOption(it.id.toString(), it.name) },
                    brands = state.brands.map { FilterOption(it.id.toString(), it.name) },
                    selectedCategoryIds = state.sheetFilterOptions.selectedCategories,
                    selectedBrandIds = state.sheetFilterOptions.selectedBrands,
                    onApply = { onIntent(SearchIntent.ApplyFilters(state.sheetFilterOptions)) },
                    onReset = { onIntent(SearchIntent.ClearFilters) },
                    onDismiss = { onIntent(SearchIntent.OnBottomSheetDismiss) },
                    onToggleCategory = { onIntent(SearchIntent.CategoriesChange(listOf(it))) },
                    onToggleBrand = { onIntent(SearchIntent.BrandsChange(listOf(it))) },
                )
            }
            SnackbarHost(
                hostState = snakBarState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp),
            )
        }

    }
}