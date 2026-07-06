package com.troves.presintation.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import com.troves.designsystem.components.toast.TrovesSnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.troves.designsystem.components.cards.MainCard
import com.troves.designsystem.components.shimmer.shimmerEffect
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.formatPrice
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.components.FilterBottomSheet
import com.troves.presintation.ui.components.FilterOption
import com.troves.presintation.ui.search.component.EmptySearchResult
import com.troves.presintation.ui.search.component.ErrorView
import com.troves.presintation.ui.search.component.ProductCardSkeleton
import com.troves.presintation.ui.search.component.RecentSearchSection
import com.troves.presintation.ui.search.component.SearchBarSection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_heart
import troves.designsystem.generated.resources.ic_star
import troves.designsystem.generated.resources.img_placeholder


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (productId: String) -> Unit,
    state: SearchUiState,
    effect: Flow<SearchEffect>,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarState = remember { SnackbarHostState() }
    val placeholder = painterResource(Res.drawable.img_placeholder)
    val starIcon = painterResource(Res.drawable.ic_star)
    val heartIcon = painterResource(Res.drawable.ic_heart)

    LaunchedEffect(Unit) {
        onIntent(SearchIntent.Load)
    }

    ObserveEffect(effect) { eff ->
        when (eff) {
            SearchEffect.NavigateBack -> onNavigateBack()
            is SearchEffect.ShowMessage -> coroutineScope.launch {
                snackBarState.showSnackbar(message = eff.message)
            }
            SearchEffect.HideKeyboard -> { /* handled by platform */ }
            is SearchEffect.NavigateToDetails -> onNavigateToDetails(eff.productId)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = Theme.colors.backGround,
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Theme.colors.backGround),
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Column {
                        SearchBarSection(
                            query = state.query,
                            onQueryChange = { onIntent(SearchIntent.SearchQueryChange(it)) },
                            onSearch = { onIntent(SearchIntent.OnSearch(it)) },
                            onBackClick = { onNavigateBack() },
                            onFilterClick = { onIntent(SearchIntent.OnFilterClick) },
                            modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
                        )

                        RecentSearchSection(
                            recentSearches = state.recentSearches,
                            onSearchClick = { onIntent(SearchIntent.OnSearch(it)) },
                            onRemoveClick = { onIntent(SearchIntent.RemoveRecentSearch(it)) },
                            onClearAllClick = { onIntent(SearchIntent.ClearRecentSearches) }
                        )
                    }
                }

                when {
                    state.isLoading -> {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(Theme.shapes.medium)
                                    .shimmerEffect()
                            )
                        }
                        items(6) {
                            ProductCardSkeleton()
                        }
                    }

                    state.hasError -> {
                        item(span = { GridItemSpan(2) }) {
                            ErrorView(
                                message = state.errorMessage.orEmpty(),
                                onRetry = { onIntent(SearchIntent.Retry) },
                            )
                        }
                    }

                    state.isEmpty -> {
                        item(span = { GridItemSpan(2) }) {
                            EmptySearchResult(modifier = Modifier.fillMaxWidth())
                        }
                    }

                    else -> {
                        items(
                            items = state.products,
                            key = { it.id }
                        ) { product ->
                            MainCard(
                                title = product.title,
                                price = formatPrice(product.price),
                                rating = product.rating.toDouble(),
                                imagePainter = rememberAsyncImagePainter(
                                    model = product.imageUrl,
                                    placeholder = placeholder,
                                    error = placeholder,
                                ),
                                ratingIconPainter = starIcon,
                                favoriteIconPainter = heartIcon,
                                onClick = { onIntent(SearchIntent.OnProductClick(product.id.toString())) },
                                onFavoriteClick = { onIntent(SearchIntent.ToggleFavorite(product.id.toString())) },
                                isFavorite = product.id.toString() in state.favoriteProductIds,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
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
            TrovesSnackbarHost(
                hostState = snackBarState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
            )
        }
    }
}