package com.troves.presintation.ui.seeall

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.SnackbarHostState
import com.troves.designsystem.components.toast.TrovesSnackbarHost
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.troves.designsystem.components.cards.MainCard
import com.troves.designsystem.components.dialog.TrovesDialog
import com.troves.designsystem.components.shimmer.shimmerEffect
import com.troves.designsystem.components.topbar.IconBox
import com.troves.domain.entity.Product
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.formatPrice
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.navigation.AppRoute
import com.troves.presintation.ui.components.NoConnectionState
import com.troves.presintation.ui.allbrands.components.BrandCard
import com.troves.presintation.ui.home.components.CategoryItem
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res as PresRes
import troves.presintation.generated.resources.common_back
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.*

@Composable
fun SeeAllScreen(
    type: AppRoute.SeeAllType,
    id: String? = null,
    name: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToProducts: (String, String, String) -> Unit,
    onNavigateToProductDetails: (String) -> Unit,
    viewModel: SeeAllViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var productToRemoveFromFav by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(type, id, name) {
        viewModel.onIntent(SeeAllIntent.Init(type, id, name))
    }

    val currentFavoriteIds by rememberUpdatedState(state.favoriteProductIds)
    val currentViewModel by rememberUpdatedState(viewModel)
    val onIntent: (SeeAllIntent) -> Unit = { intent ->
        if (intent is SeeAllIntent.ToggleFavorite && intent.product.id.toString() in currentFavoriteIds) {
            productToRemoveFromFav = intent.product
        } else {
            currentViewModel.onIntent(intent)
        }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            SeeAllEffect.NavigateBack -> onNavigateBack()
            is SeeAllEffect.NavigateToProducts -> onNavigateToProducts(effect.sourceType, effect.sourceId, effect.sourceName)
            is SeeAllEffect.NavigateToProductDetails -> onNavigateToProductDetails(effect.productId)
            is SeeAllEffect.ShowToast -> {
                scope.launch { snackbarHostState.showSnackbar(effect.message) }
            }
        }
    }

    productToRemoveFromFav?.let { product ->
        TrovesDialog(
            title = stringResource(Res.string.wishlist_remove_title),
            message = stringResource(Res.string.wishlist_remove_msg),
            confirmText = stringResource(Res.string.wishlist_remove),
            dismissText = stringResource(Res.string.profile_cancel),
            icon = painterResource(Res.drawable.ic_solid_heart),
            onConfirm = {
                viewModel.onIntent(SeeAllIntent.ToggleFavorite(product))
                productToRemoveFromFav = null
            },
            onDismiss = {
                productToRemoveFromFav = null
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SeeAllToolbar(
                title = state.title,
                onBackClick = { viewModel.onIntent(SeeAllIntent.OnBackClick) }
            )

            if (state.isLoading) {
                SeeAllShimmer(type)
            } else if (state.showOfflineState) {
                NoConnectionState(
                    onRetry = { viewModel.onIntent(SeeAllIntent.Refresh) },
                )
            } else {
                SeeAllContent(
                    state = state,
                    onIntent = onIntent,
                )
            }
        }

        TrovesSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(Theme.spacing.medium),
        )
    }
}

@Composable
private fun SeeAllToolbar(
    title: String,
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        IconBox(
            icon = painterResource(Res.drawable.ic_arrow_back),
            contentDescription = stringResource(PresRes.string.common_back),
            onClick = onBackClick,
            autoMirror = true,
        )
        BasicText(
            text = title,
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SeeAllContent(
    state: SeeAllUiState,
    onIntent: (SeeAllIntent) -> Unit,
) {
    val defaultPlaceholder = painterResource(Res.drawable.img_placeholder)
    val starIcon = painterResource(Res.drawable.ic_star)
    val heartIcon = painterResource(Res.drawable.ic_heart)

    when (state.type) {
        AppRoute.SeeAllType.CATEGORIES -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(Theme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
            ) {
                items(state.categories, key = { it.id }) { category ->
                    val categoryIcon = when (category.name.lowercase().trim()) {
                        "footwear" -> Res.drawable.ic_category_footwear
                        "outerwear" -> Res.drawable.ic_category_man
                        "accessories" -> Res.drawable.ic_category_accessories
                        "sale" -> Res.drawable.ic_category_sales
                        "new arrivals" -> Res.drawable.ic_category_sales
                        "best sellers" -> Res.drawable.ic_best_seller
                        "men" -> Res.drawable.ic_category_man
                        "kid" -> Res.drawable.ic_category_kids
                        "women" -> Res.drawable.ic_category_women
                        else -> Res.drawable.ic_star
                    }

                    CategoryItem(
                        name = category.name,
                        iconPainter = categoryIcon,
                        onClick = { onIntent(SeeAllIntent.CategoryClicked(category)) },
                        modifier = Modifier
                            .height(100.dp)
                            .animateItem()
                    )
                }
            }
        }
        AppRoute.SeeAllType.BRANDS -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
            ) {
                items(state.brands, key = { it.id }) { brand ->
                    val brandIconRes = when (brand.name.lowercase().trim()) {
                        "dr martens" -> Res.drawable.ic_brand_dr_martens
                        "herschel" -> Res.drawable.ic_brand_herschel
                        "flex fit" -> Res.drawable.ic_brand_flexfit
                        "puma" -> Res.drawable.ic_brand_puma
                        "supra" -> Res.drawable.ic_brand_supra
                        "timberland" -> Res.drawable.ic_brand_timberland
                        "converse" -> Res.drawable.ic_brand_converse
                        "asics tiger" -> Res.drawable.ic_brand_asics_tiger
                        "palladuim" -> Res.drawable.ic_brand_palladium
                        "vans" -> Res.drawable.ic_brand_vans
                        "adidas" -> Res.drawable.ic_brand_adidas
                        "nike" -> Res.drawable.ic_brand_nike
                        else -> Res.drawable.ic_star
                    }

                    BrandCard(
                        name = brand.name,
                        imagePainter = painterResource(brandIconRes),
                        onClick = { onIntent(SeeAllIntent.BrandClicked(brand)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .animateItem()
                    )
                }
            }
        }
        AppRoute.SeeAllType.PRODUCTS -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(Theme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
            ) {
                items(state.products, key = { it.id }) { product ->
                    MainCard(
                        title = product.title,
                        price = formatPrice(product.price),
                        rating = product.rating.toDouble(),
                        imagePainter = rememberAsyncImagePainter(
                            model = product.imageUrl,
                            placeholder = defaultPlaceholder,
                            error = defaultPlaceholder
                        ),
                        ratingIconPainter = starIcon,
                        favoriteIconPainter = heartIcon,
                        onClick = { onIntent(SeeAllIntent.ProductClicked(product)) },
                        onFavoriteClick = {
                            onIntent(SeeAllIntent.ToggleFavorite(product))
                        },
                        isFavorite = product.id.toString() in state.favoriteProductIds,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun SeeAllShimmer(type: AppRoute.SeeAllType) {
    when (type) {
        AppRoute.SeeAllType.CATEGORIES -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(Theme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                userScrollEnabled = false
            ) {
                items(9) {
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .clip(Theme.shapes.medium)
                            .shimmerEffect()
                    )
                }
            }
        }
        AppRoute.SeeAllType.PRODUCTS -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(Theme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                userScrollEnabled = false
            ) {
                items(6) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(Theme.shapes.medium)
                            .shimmerEffect()
                    )
                }
            }
        }
        AppRoute.SeeAllType.BRANDS -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                userScrollEnabled = false
            ) {
                items(5) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(Theme.shapes.medium)
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}