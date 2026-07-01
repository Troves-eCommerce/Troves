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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.components.dialog.TrovesDialog
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.productDetails.components.AddToCartButton
import com.troves.presintation.ui.productDetails.components.ColorSelectorRow
import com.troves.presintation.ui.productDetails.components.CustomerReviewsSection
import com.troves.presintation.ui.productDetails.components.ProductDetailTopBar
import com.troves.presintation.ui.productDetails.components.ProductDetailsShimmer
import com.troves.presintation.ui.productDetails.components.ProductImageCarousel
import com.troves.presintation.ui.productDetails.components.SectionHeaderRow
import com.troves.presintation.ui.productDetails.components.SizeSelectorRow
import com.troves.presintation.ui.productDetails.components.StarRatingRow
import com.troves.presintation.utils.Currency
import com.troves.presintation.utils.priceFormat
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ProductDetailsViewModel = koinViewModel(),
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        viewModel.onIntent(ProductDetailsIntent.Load(productId = productId))
    }

    ObserveEffect(viewModel.effect) { newEffect ->
        when (newEffect) {
            ProductDetailsEffect.NavigateBack -> onNavigateBack()
            is ProductDetailsEffect.ShowToast ->
                scope.launch { snackBarHostState.showSnackbar(newEffect.message) }
            ProductDetailsEffect.ShowLoginRequiredDialog -> showLoginRequiredDialog = true
        }
    }

    if (showLoginRequiredDialog) {
        TrovesDialog(
            title = "Login Required",
            message = "You need to be logged in to manage your favorites.",
            confirmText = "Log In",
            dismissText = "Cancel",
            onConfirm = {
                showLoginRequiredDialog = false
                onNavigateToLogin()
            },
            onDismiss = {
                showLoginRequiredDialog = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                ProductDetailsShimmer(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }

            uiState.hasError -> {
                Text("${uiState.errorMessage}")
            }

            else -> {
                val intent = viewModel::onIntent
                ProductDetailsScreenContent(
                    uiState = uiState,
                    onBackClick = { intent(ProductDetailsIntent.OnBackClick) },
                    onAddToCart = { intent(ProductDetailsIntent.OnAddToCart) },
                    onSizeSelected = { intent(ProductDetailsIntent.OnSizeSelectedChange(it)) },
                    onColorSelected = { intent(ProductDetailsIntent.OnColorSelectedChange(it)) },
                    onFavoriteClick = { intent(ProductDetailsIntent.OnFavoriteClick) },
                    onSeeAllReviews = { intent(ProductDetailsIntent.OnSeeAllReviews) },
                    onSizeGuide = { intent(ProductDetailsIntent.OnSizeGuide) },
                    modifier = Modifier.fillMaxSize().statusBarsPadding(),
                    backEnabled = true
                )
            }
        }
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
        )
    }

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
    backEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ProductDetailTopBar(
                title = "Details",
                onBackClick = onBackClick,
                modifier = Modifier.background(Theme.colors.backGround),
                enabled = backEnabled
            )
        },
        containerColor = Theme.colors.backGround,
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
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
                        .background(Theme.colors.backGround)
                        .padding(horizontal = Theme.spacing.medium),
                ) {
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = uiState.title,
                        style = Theme.typography.title,
                        fontWeight = FontWeight.Bold,
                        color = Theme.colors.primaryFont,
                        lineHeight = 30.sp,
                    )

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = uiState.priceFormatted.priceFormat(Currency.USD),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Theme.colors.primary,
                        )
                        StarRatingRow(
                            rating = uiState.rating.toFloat(),
                            reviewCount = uiState.reviewCount,
                        )
                    }

                    Spacer(Modifier.height(Theme.spacing.medium))
                    if (uiState.sizes.isNotEmpty()){
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
                    }

                    Spacer(Modifier.height(Theme.spacing.medium))

                    if (uiState.colors.isNotEmpty()){
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
                    }

                    Spacer(Modifier.height(Theme.spacing.medium))

                    SectionHeaderRow(title = "Description")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = uiState.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Theme.colors.primary,
                        lineHeight = Theme.typography.body.small.fontSize,
                    )

                    Spacer(Modifier.height(Theme.spacing.large))

                    AddToCartButton(
                        onAddToCart = onAddToCart,
                        modifier = Modifier.fillMaxWidth()
                    )

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