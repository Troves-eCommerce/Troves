package com.troves.presintation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.troves.designsystem.components.cards.MainCard
import com.troves.designsystem.components.shimmer.shimmerEffect
import com.troves.designsystem.components.topbar.TrovesTopBar
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Ad
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category
import com.troves.domain.entity.Product
import com.troves.presintation.ui.components.SignUpPromptDialog
import com.troves.presintation.ui.home.components.AdData
import com.troves.presintation.ui.home.components.AdSlider
import com.troves.presintation.ui.home.components.BrandItem
import com.troves.presintation.ui.home.components.CategoryItem
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_chevron_right
import troves.designsystem.generated.resources.ic_heart
import troves.designsystem.generated.resources.ic_star
import troves.designsystem.generated.resources.img_onboarding1

@Composable
fun HomeScreen(
    onNavigateToProduct: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToProduct -> onNavigateToProduct(effect.productId)
                is HomeEffect.NavigateToRegister -> onNavigateToRegister()
                is HomeEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    if (state.showSignUpPrompt) {
        SignUpPromptDialog(
            onConfirm = { viewModel.onIntent(HomeIntent.SignUpPromptConfirmed) },
            onDismiss = { viewModel.onIntent(HomeIntent.SignUpPromptDismissed) },
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
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            TrovesTopBar(
                onSearchClick = { viewModel.onIntent(HomeIntent.SearchClicked) },
                onCartClick = { viewModel.onIntent(HomeIntent.CartClicked) },
            )

            if (state.isLoading) {
                HomeShimmer()
            } else {
                HomeContent(
                    state = state,
                    onIntent = viewModel::onIntent,
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
        )
    }
}


@Composable
private fun HomeContent(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
) {
    val adImage = remember {
        BrushPainter(
            Brush.linearGradient(
                colors = listOf(Color(0xFF0F3D44), Color(0xFF177180)),
            ),
        )
    }
    val brandImage = painterResource(Res.drawable.img_onboarding1)
    val categoryImage = painterResource(Res.drawable.img_onboarding1)
    val productImage = painterResource(Res.drawable.img_onboarding1)
    val chevron = painterResource(Res.drawable.ic_chevron_right)
    val starIcon = painterResource(Res.drawable.ic_star)
    val heartIcon = painterResource(Res.drawable.ic_heart)

    if (state.ads.isNotEmpty()) {
        AdSlider(
            ads = state.ads.map { ad ->
                AdData(
                    titleTop = ad.titleTop,
                    titleBottom = ad.titleBottom,
                    description = ad.description,
                    imagePainter = adImage,
                    buttonText = ad.buttonText,
                )
            },
            arrowIconPainter = chevron,
            onShopNowClick = { clicked ->
                state.ads.firstOrNull { it.titleTop == clicked.titleTop }
                    ?.let { onIntent(HomeIntent.AdClicked(it)) }
            },
        )
    }

    if (state.brands.isNotEmpty()) {
        SectionHeader(
            title = "Brands",
            actionLabel = "View All",
            actionIcon = chevron,
            onAction = { onIntent(HomeIntent.SeeAllBrandsClicked) },
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.brands, key = { it.id }) { brand ->
                BrandItem(
                    name = brand.name,
                    imagePainter = rememberAsyncImagePainter(
                        model = brand.logoUrl,
                        placeholder = brandImage,
                        error = brandImage,
                    ),
                    onClick = { onIntent(HomeIntent.BrandClicked(brand)) },
                )
            }
        }
    }

    if (state.justForYou.isNotEmpty()) {
        SectionHeader(title = "Just For You")
        ProductRow(
            products = state.justForYou,
            favoriteIds = state.favoriteProductIds,
            productImage = productImage,
            starIcon = starIcon,
            heartIcon = heartIcon,
            onIntent = onIntent,
        )
    }

    if (state.categories.isNotEmpty()) {
        SectionHeader(title = "Categories")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.categories, key = { it.id }) { category ->
                CategoryItem(
                    name = category.name,
                    imagePainter = rememberAsyncImagePainter(
                        model = category.imageUrl,
                        placeholder = categoryImage,
                        error = categoryImage,
                    ),
                    onClick = { onIntent(HomeIntent.CategoryClicked(category)) },
                    modifier = Modifier.width(120.dp).height(150.dp),
                )
            }
        }
    }

    // Trending Now
    if (state.trending.isNotEmpty()) {
        SectionHeader(title = "Trending Now")
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
                price = "$${product.price}",
                rating = PLACEHOLDER_RATING,
                imagePainter = rememberAsyncImagePainter(
                    model = product.imageUrl,
                    placeholder = productImage,
                    error = productImage,
                ),
                ratingIconPainter = starIcon,
                favoriteIconPainter = heartIcon,
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
                modifier = Modifier.clickable(onClick = onAction),
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
                        modifier = Modifier.size(18.dp),
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
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
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
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                TrovesTopBar(onSearchClick = {}, onCartClick = {})
                HomeContent(state = previewHomeState(), onIntent = {})
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
        categories = List(4) { index ->
            Category(id = index.toLong(), name = "Category ${index + 1}", imageUrl = null)
        },
        trending = products,
        favoriteProductIds = setOf(0L),
    )
}
