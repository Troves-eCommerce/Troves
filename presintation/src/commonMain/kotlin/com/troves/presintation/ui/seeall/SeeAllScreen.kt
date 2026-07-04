package com.troves.presintation.ui.seeall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.navigation.AppRoute
import com.troves.presintation.ui.allbrands.components.BrandCard
import com.troves.presintation.ui.home.components.CategoryItem
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res as DesignRes
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

    LaunchedEffect(type, id, name) {
        viewModel.onIntent(SeeAllIntent.Init(type, id, name))
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            SeeAllEffect.NavigateBack -> onNavigateBack()
            is SeeAllEffect.NavigateToProducts -> onNavigateToProducts(effect.sourceType, effect.sourceId, effect.sourceName)
            is SeeAllEffect.NavigateToProductDetails -> onNavigateToProductDetails(effect.productId)
            is SeeAllEffect.ShowToast -> {} // Handle toast
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
                .padding(16.dp),
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
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBox(
            icon = painterResource(DesignRes.drawable.ic_arrow_back),
            contentDescription = "Back",
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

@Composable
private fun SeeAllContent(
    state: SeeAllUiState,
    onIntent: (SeeAllIntent) -> Unit,
) {
    when (state.type) {
        AppRoute.SeeAllType.CATEGORIES -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.categories) { category ->
                    CategoryItem(
                        name = category.name,
                        iconPainter = DesignRes.drawable.ic_star, // Default icon
                        onClick = { onIntent(SeeAllIntent.CategoryClicked(category)) },
                        modifier = Modifier.height(100.dp)
                    )
                }
            }
        }
        AppRoute.SeeAllType.BRANDS -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.brands) { brand ->
                    BrandCard(
                        name = brand.name,
                        imagePainter = rememberAsyncImagePainter(brand.logoUrl),
                        onClick = { onIntent(SeeAllIntent.BrandClicked(brand)) },
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )
                }
            }
        }
        AppRoute.SeeAllType.PRODUCTS -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.products) { product ->
                    MainCard(
                        title = product.title,
                        price = "$${product.price}",
                        rating = product.rating.toDouble(),
                        imagePainter = rememberAsyncImagePainter(product.imageUrl),
                        ratingIconPainter = painterResource(DesignRes.drawable.ic_star),
                        favoriteIconPainter = painterResource(DesignRes.drawable.ic_heart),
                        onClick = { onIntent(SeeAllIntent.ProductClicked(product)) },
                        onFavoriteClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SeeAllShimmer(type: AppRoute.SeeAllType) {
    // Basic shimmer implementation
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (type == AppRoute.SeeAllType.BRANDS) 140.dp else 100.dp)
                    .clip(Theme.shapes.medium)
                    .shimmerEffect()
            )
        }
    }
}
