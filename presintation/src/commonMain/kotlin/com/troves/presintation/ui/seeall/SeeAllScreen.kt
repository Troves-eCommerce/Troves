package com.troves.presintation.ui.seeall

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.troves.designsystem.components.cards.MainCard
import com.troves.designsystem.components.shimmer.shimmerEffect
import com.troves.designsystem.components.topbar.IconBox
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.formatPrice
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.navigation.AppRoute
import com.troves.presintation.ui.allbrands.components.BrandCard
import com.troves.presintation.ui.home.components.CategoryItem
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
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

    LaunchedEffect(type, id, name) {
        viewModel.onIntent(SeeAllIntent.Init(type, id, name))
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
            } else {
                SeeAllContent(
                    state = state,
                    onIntent = viewModel::onIntent
                )
            }
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
            contentDescription = "Back",
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
                        "best sellers" -> Res.drawable.ic_star
                        "men" -> Res.drawable.ic_category_man
                        "women" -> Res.drawable.ic_category_women
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
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(Theme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
            ) {
                items(state.brands, key = { it.id }) { brand ->
                    val brandIconRes = when (brand.name.lowercase().trim()) {
                        "footwear" -> Res.drawable.ic_category_footwear
                        "outerwear" -> Res.drawable.ic_category_man
                        "accessories" -> Res.drawable.ic_category_accessories
                        "sale" -> Res.drawable.ic_category_sales
                        "new arrivals" -> Res.drawable.ic_category_sales
                        "best sellers" -> Res.drawable.ic_star
                        "men" -> Res.drawable.ic_category_man
                        "women" -> Res.drawable.ic_category_women
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
                        "kids" -> Res.drawable.ic_category_kids
                        else -> Res.drawable.ic_star
                    }

                    BrandCard(
                        name = brand.name,
                        imagePainter = painterResource(brandIconRes),
                        onClick = { onIntent(SeeAllIntent.BrandClicked(brand)) },
                        modifier = Modifier
                            .height(100.dp)
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
                        onFavoriteClick = { onIntent(SeeAllIntent.ToggleFavorite(product)) },
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
    }
}