package com.troves.presintation.ui.search.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Product
import com.troves.presintation.ui.search.SearchIntent
import com.troves.presintation.ui.search.SearchUiState

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
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchContent(
    state: SearchUiState,
    modifier: Modifier = Modifier,
    onIntent: (SearchIntent) -> Unit,
) {

    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxSize().background(
            color = Theme.colors.backGround
        ),
        columns = StaggeredGridCells.Fixed(3),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 32.dp
        ),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item(
            span = StaggeredGridItemSpan.FullLine
        ) {

            SearchBarSection(
                modifier = Modifier.fillMaxWidth(),
                query = state.query ?: "",
                onQueryChange = {
                    onIntent(
                        SearchIntent.SearchQueryChange(it)
                    )
                },
                onSearch = {
                    onIntent(
                        SearchIntent.OnSearch(it)
                    )
                },
                onFilterClick = {
                    onIntent(SearchIntent.OnFilterClick)
                },
                searchSuggestions =  buildList {
                    state.categories.map { it.name }.forEach { add(it) }
                    state.brands.map { it.name }.forEach { add(it) }
                },
            )

        }

        item(
            span = StaggeredGridItemSpan.FullLine
        ) {

            RecentSearchSection(
                recentSearches = state.recentSearches.toList(),
                onSearchClick = {
                    onIntent(
                        SearchIntent.SearchQueryChange(it)
                    )
                },
                onRemoveClick = {
                    //TODO()
                }
            )

        }

        item(
            span = StaggeredGridItemSpan.FullLine
        ) {

            BrandRow(
                brands = state.brands,
                selectedBrand = state.brands[0].name,
                onBrandSelected = {
                    onIntent(
                        SearchIntent.BrandChange(it.id.toString())
                    )
                }
            )

        }

        items(
            items = state.products,
            key = Product::id
        ) { product ->

            ProductCard(
                product = product,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                onClick = {
                    onIntent(SearchIntent.OnProductClick(product.id.toString()))
                }
            )

        }

    }

}