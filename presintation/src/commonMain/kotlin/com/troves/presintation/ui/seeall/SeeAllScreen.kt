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
        val toolbarTitle = when (state.type) {
            AppRoute.SeeAllType.CATEGORIES -> stringResource(Res.string.seeall_categories_title)
            AppRoute.SeeAllType.BRANDS -> stringResource(Res.string.seeall_brands_title)
            AppRoute.SeeAllType.PRODUCTS -> state.title
        }

        Column(modifier = Modifier.fillMaxSize()) {
            SeeAllToolbar(
                title = toolbarTitle,
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
                        "footwear", "الأحذية" -> Res.drawable.ic_category_footwear
                        "outerwear", "ملابس خارجية" -> Res.drawable.ic_category_man
                        "accessories", "اكسسوارات" -> Res.drawable.ic_category_accessories
                        "sale", "تخفيضات" -> Res.drawable.ic_category_sales
                        "new arrivals", "وصل حديثا" -> Res.drawable.ic_category_sales
                        "best sellers", "الاعلي مبيعا"->Res.drawable.ic_best_seller
                        "men", "رجال" -> Res.drawable.ic_category_man
                        "women", "نسائي" -> Res.drawable.ic_category_women
                        "dr martens", "دكتور مارتنز" -> Res.drawable.ic_brand_dr_martens
                        "kid", "اطفال" -> Res.drawable.ic_category_kids
                        else -> Res.drawable.ic_star
                    }

                    val categoryTitle = when (category.name.lowercase().trim()) {
                        "footwear", "الأحذية" -> stringResource(Res.string.category_footwear)
                        "outerwear", "ملابس خارجية" -> stringResource(Res.string.category_outerwear)
                        "accessories", "اكسسوارات" -> stringResource(Res.string.category_accessories)
                        "sale", "تخفيضات" -> stringResource(Res.string.category_sale)
                        "new arrivals", "وصل حديثا" -> stringResource(Res.string.category_new_arrivals)
                        "best sellers", "الاعلي مبيعا" -> stringResource(Res.string.category_best_sellers)
                        "men", "رجال" -> stringResource(Res.string.category_men)
                        "women", "نسائي" -> stringResource(Res.string.category_women)
                        "dr martens", "دكتور مارتنز" -> stringResource(Res.string.brand_dr_martens)
                        "kid", "اطفال" -> stringResource(Res.string.category_kids)
                        else -> category.name
                    }

                    CategoryItem(
                        name = categoryTitle,
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

                    val brandTitle = when (brand.name.lowercase().trim()) {
                        "dr martens" -> stringResource(Res.string.brand_dr_martens)
                        "herschel" -> stringResource(Res.string.brand_herschel)
                        "flex fit" -> stringResource(Res.string.brand_flexfit)
                        "puma" -> stringResource(Res.string.brand_puma)
                        "supra" -> stringResource(Res.string.brand_supra)
                        "timberland" -> stringResource(Res.string.brand_timberland)
                        "converse" -> stringResource(Res.string.brand_converse)
                        "asics tiger" -> stringResource(Res.string.brand_asics_tiger)
                        "palladuim" -> stringResource(Res.string.brand_palladium)
                        "vans" -> stringResource(Res.string.brand_vans)
                        "adidas" -> stringResource(Res.string.brand_adidas)
                        "nike" -> stringResource(Res.string.brand_nike)
                        else -> brand.name
                    }

                    BrandCard(
                        name = brandTitle,
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