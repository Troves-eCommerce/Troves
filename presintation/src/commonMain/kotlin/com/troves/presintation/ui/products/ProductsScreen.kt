package com.troves.presintation.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.cards.MainCard
import com.troves.designsystem.components.chip.AppChip
import com.troves.designsystem.components.shimmer.shimmerEffect
import com.troves.designsystem.components.topbar.IconBox
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Product
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.components.FilterBottomSheet
import com.troves.presintation.ui.components.SortBottomSheet
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res as DesignRes
import troves.designsystem.generated.resources.ic_arrow_back
import troves.designsystem.generated.resources.ic_heart
import troves.designsystem.generated.resources.ic_star
import troves.designsystem.generated.resources.img_onboarding1
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.filter_title
import troves.presintation.generated.resources.products_empty
import troves.presintation.generated.resources.products_retry
import troves.presintation.generated.resources.products_title
import troves.presintation.generated.resources.sort_title

@Composable
fun ProductsScreen(
    onNavigateToProduct: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: ProductsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is ProductsEffect.NavigateToProduct -> onNavigateToProduct(effect.productId)
            is ProductsEffect.NavigateBack -> onNavigateBack()
            is ProductsEffect.ShowToast ->
                scope.launch { snackbarHostState.showSnackbar(effect.message) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            ProductsToolbar(
                filterActive = state.hasActiveFilters,
                sortActive = state.isSorted,
                onBackClick = { viewModel.onIntent(ProductsIntent.OnBackClick) },
                onFilterClick = { viewModel.onIntent(ProductsIntent.OpenFilter) },
                onSortClick = { viewModel.onIntent(ProductsIntent.OpenSort) },
            )

            when {
                state.isLoading -> ProductsShimmer()

                state.hasError -> ProductsError(
                    message = state.errorMessage.orEmpty(),
                    onRetry = { viewModel.onIntent(ProductsIntent.Retry) },
                )

                else -> ProductsGrid(
                    products = state.displayedProducts,
                    onProductClick = { viewModel.onIntent(ProductsIntent.ProductClicked(it)) },
                )
            }
        }

        if (state.showFilterSheet) {
            FilterBottomSheet(
                categories = state.categoryOptions,
                subCategories = state.subCategoryOptions,
                brands = state.brandOptions,
                selectedCategoryIds = state.draftCategoryIds,
                selectedSubCategoryIds = state.draftSubCategoryIds,
                selectedBrandIds = state.draftBrandIds,
                onToggleCategory = { viewModel.onIntent(ProductsIntent.ToggleCategory(it)) },
                onToggleSubCategory = { viewModel.onIntent(ProductsIntent.ToggleSubCategory(it)) },
                onToggleBrand = { viewModel.onIntent(ProductsIntent.ToggleBrand(it)) },
                onApply = { viewModel.onIntent(ProductsIntent.ApplyFilter) },
                onReset = { viewModel.onIntent(ProductsIntent.ResetFilter) },
                onDismiss = { viewModel.onIntent(ProductsIntent.DismissSheet) },
            )
        }

        if (state.showSortSheet) {
            SortBottomSheet(
                selected = state.draftSort,
                onSelect = { viewModel.onIntent(ProductsIntent.SelectSort(it)) },
                onApply = { viewModel.onIntent(ProductsIntent.ApplySort) },
                onDismiss = { viewModel.onIntent(ProductsIntent.DismissSheet) },
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(Theme.spacing.medium),
        )
    }
}

@Composable
private fun ProductsToolbar(
    filterActive: Boolean,
    sortActive: Boolean,
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        IconBox(
            icon = painterResource(DesignRes.drawable.ic_arrow_back),
            contentDescription = "Navigate up",
            onClick = onBackClick,
        )
        BasicText(
            text = stringResource(Res.string.products_title),
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
        Box(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
            AppChip(
                label = stringResource(Res.string.filter_title),
                selected = filterActive,
                onClick = onFilterClick,
            )
            AppChip(
                label = stringResource(Res.string.sort_title),
                selected = sortActive,
                onClick = onSortClick,
            )
        }
    }
}

@Composable
private fun ProductsGrid(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
) {
    if (products.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = stringResource(Res.string.products_empty),
                style = Theme.typography.body.large.copy(color = Theme.colors.secondaryFont),
            )
        }
        return
    }

    val placeholder = painterResource(DesignRes.drawable.img_onboarding1)
    val starIcon = painterResource(DesignRes.drawable.ic_star)
    val heartIcon = painterResource(DesignRes.drawable.ic_heart)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Theme.spacing.medium,
            end = Theme.spacing.medium,
            bottom = Theme.spacing.large,
        ),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        items(products, key = { it.id }) { product ->
            MainCard(
                title = product.title,
                price = "$${product.price}",
                rating = product.rating.toDouble(),
                imagePainter = rememberAsyncImagePainter(
                    model = product.imageUrl,
                    placeholder = placeholder,
                    error = placeholder,
                ),
                ratingIconPainter = starIcon,
                favoriteIconPainter = heartIcon,
                onClick = { onProductClick(product) },
                onFavoriteClick = { onProductClick(product) },
                containerColor = Theme.colors.backGround,
            )
        }
    }
}

@Composable
private fun ProductsShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)) {
                repeat(2) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(Theme.shapes.medium)
                            .background(Theme.colors.surface),
                        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .shimmerEffect(),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(14.dp)
                                .padding(start = Theme.spacing.small)
                                .clip(Theme.shapes.small)
                                .shimmerEffect(),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(14.dp)
                                .padding(start = Theme.spacing.small, bottom = Theme.spacing.small)
                                .clip(Theme.shapes.small)
                                .shimmerEffect(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductsError(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Theme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium, Alignment.CenterVertically),
    ) {
        BasicText(
            text = message,
            style = Theme.typography.body.large.copy(color = Theme.colors.error),
        )
        PrimaryButton(
            caption = stringResource(Res.string.products_retry),
            onClick = onRetry,
        )
    }
}
