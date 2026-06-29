package com.troves.presintation.ui.productDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.home.HomeViewModel
import com.troves.presintation.ui.productDetails.components.AddToCartButton
import com.troves.presintation.ui.productDetails.components.ColorSelectorRow
import com.troves.presintation.ui.productDetails.components.CustomerReviewsSection
import com.troves.presintation.ui.productDetails.components.ProductDetailTopBar
import com.troves.presintation.ui.productDetails.components.ProductImageCarousel
import com.troves.presintation.ui.productDetails.components.SectionHeaderRow
import com.troves.presintation.ui.productDetails.components.SizeSelectorRow
import com.troves.presintation.ui.productDetails.components.StarRatingRow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductDetailsScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    viewModel: ProductDetailsViewModel = koinViewModel(),
) {

    val uiState = viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.onIntent(intent = ProductDetailsIntent.Load(productId = productId))
        val effect = viewModel.effect.collect { newEffect ->
            when (newEffect) {
                ProductDetailsEffect.NavigateBack -> {
                    onNavigateBack()
                }

                is ProductDetailsEffect.ShowToast -> {
                    snackBarHostState.showSnackbar(newEffect.message)
                }
            }
        }

    }

    ProductDetailsScreenContent(
        uiState = TODO(),
        onBackClick = TODO(),
        onAddToCart = TODO(),
        onSizeSelected = TODO(),
        onColorSelected = TODO(),
        onFavoriteClick = TODO(),
        onSeeAllReviews = TODO(),
        onSizeGuide = TODO()
    )


}

@Composable
fun ProductDetailsScreenContent(
    uiState: ProductDetailUiState,
    onBackClick: () -> Unit,
    onAddToCart: () -> Unit,
    onSizeSelected: (String) -> Unit,
    onColorSelected: (Int) -> Unit,
    onFavoriteClick: () -> Unit,
    onSeeAllReviews: () -> Unit,
    onSizeGuide: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ProductDetailTopBar(
                title = "Details",
                onBackClick = onBackClick,
            )
        },
        containerColor = Color.White,
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            item {
                ProductImageCarousel(
                    imageUrls = uiState.images,
                    currentIndex = uiState.currentImageIndex,
                    isFavorite = uiState.isFavorite,
                    onFavoriteClick = onFavoriteClick,
                )
            }


            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = Theme.spacing.medium),
                ) {
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = uiState.title,
                        style = Theme.typography.title,
                        fontWeight = FontWeight.Bold,
                        color = Theme.colors.primaryVariant,
                        lineHeight = 30.sp,
                    )

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = uiState.priceFormatted,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Theme.colors.primary,
                        )
                        StarRatingRow(
                            rating = uiState.rating,
                            reviewCount = uiState.reviewCount,
                        )
                    }

                    Spacer(Modifier.height(Theme.spacing.medium))

                    SectionHeaderRow(
                        title = "Size",
                        actionLabel = "Size Guide",
                        onActionClick = onSizeGuide,
                    )
                    Spacer(Modifier.height(10.dp))
                    SizeSelectorRow(
                        sizes = uiState.sizes,
                        selectedSizeLabel = uiState.selectedSizeLabel,
                        onSizeSelected = onSizeSelected,
                    )

                    Spacer(Modifier.height(Theme.spacing.medium))


                    SectionHeaderRow(
                        title = "Colors",
                        actionLabel = "Color Guide",
                        onActionClick = onSizeGuide,
                    )
                    Spacer(Modifier.height(10.dp))
                    ColorSelectorRow(
                        colors = uiState.colors,
                        selectedColorIndex = uiState.selectedColorIndex,
                        onColorSelected = onColorSelected,
                    )

                    Spacer(Modifier.height(Theme.spacing.medium))

                    SectionHeaderRow(title = "Description")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = uiState.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Theme.colors.primaryVariant,
                        lineHeight = Theme.typography.body.small.fontSize,
                    )

                    Spacer(Modifier.height(Theme.spacing.large))


                    AddToCartButton(onAddToCart = onAddToCart)

                    Spacer(Modifier.height(Theme.spacing.large))


                    CustomerReviewsSection(
                        reviews = uiState.reviews,
                        onSeeAllClick = onSeeAllReviews,
                    )


                    Spacer(Modifier.height(Theme.spacing.extraLarge))
                }
            }
        }
    }
}