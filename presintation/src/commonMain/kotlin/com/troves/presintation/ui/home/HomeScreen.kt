package com.troves.presintation.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import com.troves.designsystem.components.toast.TrovesSnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.troves.designsystem.components.cards.MainCard
import com.troves.designsystem.components.shimmer.shimmerEffect
import com.troves.designsystem.components.topbar.TrovesTopBar
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.formatPrice
import com.troves.designsystem.util.autoMirror
import com.troves.designsystem.util.bounceClick
import kotlin.math.abs

import com.troves.designsystem.components.dialog.TrovesDialog
import com.troves.domain.entity.Ad
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category
import com.troves.domain.entity.Product
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.components.NoConnectionState
import com.troves.presintation.ui.components.SignUpPromptDialog
import com.troves.presintation.ui.home.components.AdData
import com.troves.presintation.ui.home.components.AdSlider
import com.troves.presintation.ui.home.components.BrandItem
import com.troves.presintation.ui.home.components.CategoryItem
import com.troves.presintation.ui.survey.components.SurveyBannerCard
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.*

@Composable
fun HomeScreen(
    onNavigateToProduct: (String) -> Unit,
    onNavigateToProducts: (sourceType: String, sourceId: String, sourceName: String) -> Unit,
    onNavigateToAllBrands: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToAiChat: () -> Unit,
    onNavigateToAllCategories: () -> Unit,
    onNavigateToSurvey: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSurveySheet by remember { mutableStateOf(false) }
    var dismissedSurveyPopup by remember { mutableStateOf(false) }
    var productToRemove by remember { mutableStateOf<Product?>(null) }

    val loginRequiredText = stringResource(Res.string.home_login_required)
    val showSurveyPopup = state.isLoggedIn && !state.isSurveyDone && !dismissedSurveyPopup

    // Intercept wishlist REMOVALS to confirm first; adding a favorite (or any other intent) passes through.
    val onIntent: (HomeIntent) -> Unit = { intent ->
        if (intent is HomeIntent.FavoriteToggled && intent.product.id in state.favoriteProductIds) {
            productToRemove = intent.product
        } else {
            viewModel.onIntent(intent)
        }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is HomeEffect.NavigateToProduct -> onNavigateToProduct(effect.productId)
            is HomeEffect.NavigateToProducts -> onNavigateToProducts(
                effect.sourceType,
                effect.sourceId,
                effect.sourceName,
            )
            is HomeEffect.NavigateToAllBrands -> onNavigateToAllBrands()
            is HomeEffect.NavigateToRegister -> onNavigateToRegister()
            is HomeEffect.NavigateToAllCategories -> onNavigateToAllCategories()
            is HomeEffect.NavigateToCart -> onNavigateToCart()
            is HomeEffect.NavigateToSurvey -> showSurveySheet = true
            is HomeEffect.ShowToast -> scope.launch { snackbarHostState.showSnackbar(effect.message) }
            is HomeEffect.NavigateToSearch -> onNavigateToSearch()
            is HomeEffect.ShowLoginRequiredDialog -> scope.launch {
                snackbarHostState.showSnackbar(loginRequiredText)
            }

            HomeEffect.NavigateToAiChat -> onNavigateToAiChat()
        }
    }

    if (showSurveySheet) {
        com.troves.presintation.ui.survey.SurveyBottomSheet(
            onDismiss = { showSurveySheet = false },
        )
    }

    if (state.showSignUpPrompt) {
        SignUpPromptDialog(
            onConfirm = { viewModel.onIntent(HomeIntent.SignUpPromptConfirmed) },
            onDismiss = { viewModel.onIntent(HomeIntent.SignUpPromptDismissed) },
        )
    }

    productToRemove?.let { product ->
        TrovesDialog(
            title = stringResource(Res.string.wishlist_remove_title),
            message = stringResource(Res.string.wishlist_remove_msg),
            confirmText = stringResource(Res.string.wishlist_remove),
            dismissText = stringResource(Res.string.profile_cancel),
            icon = painterResource(Res.drawable.ic_solid_heart),
            onConfirm = {
                viewModel.onIntent(HomeIntent.FavoriteToggled(product))
                productToRemove = null
            },
            onDismiss = { productToRemove = null },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .then(if (showSurveyPopup) Modifier.blur(16.dp) else Modifier),
        ) {
            TrovesTopBar(
                onSearchClick = { viewModel.onIntent(HomeIntent.SearchClicked) },
                onCartClick = { viewModel.onIntent(HomeIntent.CartClicked) },
                onAiClick = { viewModel.onIntent(HomeIntent.AiClicked)},
                cartBadgeCount = state.cartItemCount,
                border = BorderStroke(
                    width = 1.dp,
                    color = Theme.colors.onPrimary
                )
            )

            if (state.showOfflineState) {
                NoConnectionState(
                    onRetry = { viewModel.onIntent(HomeIntent.Retry) },
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 20.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    if (state.isLoading) {
                        HomeShimmer()
                    } else {
                        HomeContent(
                            state = state,
                            onIntent = onIntent,
                        )
                    }
                }
            }
        }

        TrovesSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(16.dp),
        )

        if (showSurveyPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {},
                contentAlignment = Alignment.Center
            ) {
                SurveyBannerCard(
                    onStartSurvey = { viewModel.onIntent(HomeIntent.SurveyBannerClicked) },
                    onDismiss = { dismissedSurveyPopup = true },
                    onNeverShowAgain = { viewModel.onIntent(HomeIntent.SurveyBannerNeverShowAgain) },
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
) {
    val brandImage = painterResource(Res.drawable.img_placeholder)
    val productImage = painterResource(Res.drawable.img_placeholder)
    val chevron = painterResource(Res.drawable.ic_chevron_right)
    val starIcon = painterResource(Res.drawable.ic_star)
    val heartIcon = painterResource(Res.drawable.ic_solid_heart)

    val adImages = listOf(
        Res.drawable.random_7,
        Res.drawable.random_2,
        Res.drawable.random_3,
        Res.drawable.random_4,
        Res.drawable.random_5,
        Res.drawable.random_6,
        Res.drawable.random_1,
        Res.drawable.random_8
    )

    val clipboardManager = LocalClipboardManager.current
    val copyCodeButtonText = stringResource(Res.string.home_copy_code_button)

    if (state.ads.isNotEmpty()) {
        AdSlider(
            ads = state.ads.mapIndexed { index, ad ->
                val imageRes = adImages[index % adImages.size]
                AdData(
                    titleTop = ad.titleTop,
                    titleBottom = ad.titleBottom,
                    description = ad.description,
                    imagePainter = painterResource(imageRes),
                    buttonText = ad.buttonText,
                )
            },
            arrowIconPainter = chevron,
            onShopNowClick = { clicked ->
                val ad = state.ads.firstOrNull { it.titleTop == clicked.titleTop }
                if (ad != null) {
                    if (ad.buttonText == copyCodeButtonText) {
                        clipboardManager.setText(AnnotatedString(ad.titleTop))
                    }
                    onIntent(HomeIntent.AdClicked(ad))
                }
            },
        )
    }

    if (state.categories.isNotEmpty()) {
        SectionHeader(
            title = stringResource(Res.string.home_categories_title),
            actionIcon = chevron,
            actionLabel = stringResource(Res.string.home_view_all),
            onAction = { onIntent(HomeIntent.ViewAllCategoriesClicked) }
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.categories.take(5), key = { it.id }) { category ->
                val categoryIcon = when (category.name.lowercase().trim()) {
                    "footwear" -> Res.drawable.ic_category_footwear
                    "outerwear" -> Res.drawable.ic_category_man
                    "accessories" -> Res.drawable.ic_category_accessories
                    "sale" -> Res.drawable.ic_category_sales
                    "new arrivals" -> Res.drawable.ic_category_sales
                    "best sellers" -> Res.drawable.ic_best_seller
                    "men" -> Res.drawable.ic_category_man
                    "women" -> Res.drawable.ic_category_women
                    "dr martens" -> Res.drawable.ic_brand_dr_martens
                    "kid" -> Res.drawable.ic_category_kids
                    else -> Res.drawable.ic_star
                }

                CategoryItem(
                    name = category.name,
                    iconPainter = categoryIcon,
                    onClick = { onIntent(HomeIntent.CategoryClicked(category)) },
                    modifier = Modifier
                        .width(85.dp)
                        .height(100.dp),
                )
            }
        }
    }

    if (state.justForYou.isNotEmpty()) {
        SectionHeader(
            title = stringResource(Res.string.see_all),
            actionIcon = chevron,
            actionLabel = stringResource(Res.string.see_all),
            onAction = { onIntent(HomeIntent.ViewAllJustForYouClicked) }
        )
        ProductRow(
            products = state.justForYou,
            favoriteIds = state.favoriteProductIds,
            productImage = productImage,
            starIcon = starIcon,
            heartIcon = heartIcon,
            onIntent = onIntent,
        )
    }
    if (state.brands.isNotEmpty()) {
        SectionHeader(
            title = stringResource(Res.string.home_top_brands),
            actionLabel = stringResource(Res.string.home_view_all_brands),
            actionIcon = chevron,
            onAction = { onIntent(HomeIntent.SeeAllBrandsClicked) },
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.brands.take(5), key = { it.id }) { brand ->

                val localBrandImage = when (brand.name.trim().lowercase()) {
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
                    else -> null
                }

                BrandItem(
                    name = brand.name,
                    imagePainter = if (localBrandImage != null) {
                        painterResource(localBrandImage)
                    } else {
                        rememberAsyncImagePainter(
                            model = brand.logoUrl,
                            placeholder = brandImage,
                            error = brandImage,
                        )
                    },
                    onClick = {
                        onIntent(HomeIntent.BrandClicked(brand))
                    },
                )
            }
        }
    }

    if (state.trending.isNotEmpty()) {
        SectionHeader(
            title = stringResource(Res.string.see_all),
            actionIcon = chevron,
            actionLabel = stringResource(Res.string.see_all),
            onAction = { onIntent(HomeIntent.ViewAllTrendingClicked) }
        )
        ProductRow(
            products = state.trending,
            favoriteIds = state.favoriteProductIds,
            productImage = productImage,
            starIcon = starIcon,
            heartIcon = heartIcon,
            onIntent = onIntent,
        )
    }
}

@Composable
private fun ProductRow(
    products: List<Product>,
    favoriteIds: Set<Long>,
    productImage: Painter,
    starIcon: Painter,
    heartIcon: Painter,
    onIntent: (HomeIntent) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(products, key = { it.id }) { product ->
            MainCard(
                title = product.title,
                price = formatPrice(product.price),
                rating = PLACEHOLDER_RATING,
                imagePainter = rememberAsyncImagePainter(
                    model = product.imageUrl,
                    placeholder = productImage,
                    error = productImage,
                ),
                ratingIconPainter = starIcon,
                favoriteIconPainter = heartIcon,
                isFavorite = product.id in favoriteIds,
                onClick = { onIntent(HomeIntent.ProductClicked(product)) },
                onFavoriteClick = { onIntent(HomeIntent.FavoriteToggled(product)) },
                modifier = Modifier.width(170.dp),
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    actionIcon: Painter? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = title,
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.bounceClick(
                    shape = RoundedCornerShape(10.dp),
                    onClick = onAction
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    text = actionLabel,
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.Medium,
                    ),
                )
                if (actionIcon != null) {
                    Icon(
                        painter = actionIcon,
                        contentDescription = null,
                        tint = Theme.colors.secondaryFont,
                        modifier = Modifier
                            .size(18.dp)
                            .autoMirror(),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeShimmer() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(180.dp)
            .clip(Theme.shapes.medium)
            .shimmerEffect(),
    )

    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .width(85.dp)
                    .height(100.dp)
                    .clip(Theme.shapes.medium)
                    .shimmerEffect(),
            )
        }
    }

    repeat(2) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .width(170.dp)
                        .height(240.dp)
                        .clip(Theme.shapes.medium)
                        .shimmerEffect(),
                )
            }
        }
    }
}

private const val PLACEHOLDER_RATING = 4.5

@Preview
@Composable
private fun HomeScreenPreview() {
    SpTheme {
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
                TrovesTopBar(onSearchClick = {}, onCartClick = {}, onAiClick = {})
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 20.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    HomeContent(state = previewHomeState(), onIntent = {})
                }
            }
        }
    }
}

private fun previewHomeState(): HomeUiState {
    val products = List(4) { index ->
        Product(
            id = index.toLong(),
            title = "Air Zoom Pegasus ${index + 1}",
            vendor = "Nike",
            price = "${120 + index * 10}.00",
            imageUrl = "null",
            status = "active",
        )
    }
    return HomeUiState(
        isLoading = false,
        ads = listOf(
            Ad(
                id = 1L,
                titleTop = "Summer",
                titleBottom = "Collection",
                description = "Up to 50% off on selected items",
                buttonText = "Shop now",
            ),
        ),
        brands = List(6) { index ->
            Brand(id = index.toLong(), name = "Brand ${index + 1}", logoUrl = null)
        },
        justForYou = products,
        categories = List(6) { index ->
            Category(id = index.toLong(), name = "Category ${index + 1}", imageUrl = null)
        },
        trending = products,
        favoriteProductIds = setOf(0L),
    )
}