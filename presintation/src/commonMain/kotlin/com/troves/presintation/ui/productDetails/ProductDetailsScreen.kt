package com.troves.presintation.ui.productDetails

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import com.troves.designsystem.components.button.PrimaryButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.components.dialog.LoginRequiredDialog
import com.troves.designsystem.components.dialog.TrovesDialog
import com.troves.designsystem.components.toast.TrovesSnackbarHost
import org.jetbrains.compose.resources.painterResource
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.formatPrice
import com.troves.designsystem.util.stripHtml
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.components.NoConnectionState
import com.troves.presintation.ui.productDetails.components.AddToCartButton
import com.troves.presintation.ui.productDetails.components.CustomerReviewsSection
import com.troves.presintation.ui.productDetails.components.OptionSelectorRow
import com.troves.presintation.ui.productDetails.components.ProductDetailsShimmer
import com.troves.presintation.ui.productDetails.components.ProductImageCarousel
import com.troves.presintation.ui.productDetails.components.ReviewsBottomSheet
import com.troves.presintation.ui.productDetails.components.SectionHeaderRow
import com.troves.presintation.ui.productDetails.components.StarRatingRow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.cart_checkout
import troves.designsystem.generated.resources.product_details_added_to_cart
import troves.designsystem.generated.resources.product_details_color_label
import troves.designsystem.generated.resources.product_details_description
import troves.designsystem.generated.resources.product_details_quantity_in_cart
import troves.designsystem.generated.resources.product_details_read_less
import troves.designsystem.generated.resources.product_details_read_more
import troves.designsystem.generated.resources.product_details_review_login_required
import troves.designsystem.generated.resources.product_details_size_guide
import troves.designsystem.generated.resources.product_details_view_cart
import troves.designsystem.generated.resources.product_details_login_required_favorites
import troves.designsystem.generated.resources.wishlist_remove_title
import troves.designsystem.generated.resources.wishlist_remove_msg
import troves.designsystem.generated.resources.wishlist_remove
import troves.designsystem.generated.resources.profile_cancel
import troves.designsystem.generated.resources.ic_solid_heart
import troves.designsystem.generated.resources.product_details_size

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: ProductDetailsViewModel = koinViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showLoginRequiredDialog by remember { mutableStateOf(false) }
    var loginDialogForReview by remember { mutableStateOf(false) }
    var showRemoveFavoriteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        viewModel.onIntent(ProductDetailsIntent.Load(productId = productId))
    }

    ObserveEffect(viewModel.effect) { newEffect ->
        when (newEffect) {
            ProductDetailsEffect.NavigateBack -> onNavigateBack()
            is ProductDetailsEffect.ShowToast ->
                scope.launch { snackBarHostState.showSnackbar(newEffect.message) }
            is ProductDetailsEffect.ShowLoginRequiredDialog -> {
                loginDialogForReview = newEffect.forReview
                showLoginRequiredDialog = true
            }
            ProductDetailsEffect.NavigateToCart -> onNavigateToCart()
        }
    }

    if (showLoginRequiredDialog) {
        LoginRequiredDialog(
            message = if (loginDialogForReview) {
                stringResource(Res.string.product_details_review_login_required)
            } else {
                stringResource(Res.string.product_details_login_required_favorites)
            },
            onLoginClick = {
                showLoginRequiredDialog = false
                onNavigateToLogin()
            },
            onDismiss = { showLoginRequiredDialog = false }
        )
    }

    if (showRemoveFavoriteDialog) {
        TrovesDialog(
            title = stringResource(Res.string.wishlist_remove_title),
            message = stringResource(Res.string.wishlist_remove_msg),
            confirmText = stringResource(Res.string.wishlist_remove),
            dismissText = stringResource(Res.string.profile_cancel),
            onConfirm = {
                showRemoveFavoriteDialog = false
                viewModel.onIntent(ProductDetailsIntent.OnFavoriteClick)
            },
            onDismiss = { showRemoveFavoriteDialog = false },
        )
    }

    if (uiState.showReviewsSheet) {
        ReviewsBottomSheet(
            reviews = uiState.reviews,
            myReview = uiState.myReview,
            showEditor = uiState.showReviewEditor,
            draft = uiState.reviewDraft,
            isSubmitting = uiState.isSubmittingReview,
            onDraftChange = { viewModel.onIntent(ProductDetailsIntent.OnReviewDraftChanged(it)) },
            onWriteReview = { viewModel.onIntent(ProductDetailsIntent.OnOpenReviewEditor) },
            onDismissEditor = { viewModel.onIntent(ProductDetailsIntent.OnDismissReviewEditor) },
            onSubmit = { viewModel.onIntent(ProductDetailsIntent.OnSubmitReview) },
            onDismiss = { viewModel.onIntent(ProductDetailsIntent.OnDismissReviewsSheet) },
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
                ProductDetailsShimmer(modifier = Modifier.fillMaxSize())
            }
            uiState.showOfflineState -> {
                NoConnectionState(
                    onRetry = { viewModel.onIntent(ProductDetailsIntent.Retry(productId)) },
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
                    onOptionSelected = { name, value ->
                        intent(ProductDetailsIntent.OnOptionSelected(name, value))
                    },
                    onFavoriteClick = {
                        if (uiState.isFavorite) {
                            showRemoveFavoriteDialog = true      // removing → confirm
                        } else {
                            intent(ProductDetailsIntent.OnFavoriteClick) // adding → instant
                        }
                    },
                    onSeeAllReviews = { intent(ProductDetailsIntent.OnSeeAllReviews) },
                    onWriteReview = { intent(ProductDetailsIntent.OnOpenReviewEditor) },
                    onSizeGuide = { intent(ProductDetailsIntent.OnSizeGuide) },
                    modifier = Modifier.fillMaxSize(),
                    backEnabled = true
                )
            }
        }

        TrovesSnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(16.dp),
        )

        AnimatedVisibility(
            visible = uiState.showCartConfirmation,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            CartConfirmationBar(
                quantity = uiState.productCartQuantity,
                onViewCartClick = { viewModel.onIntent(ProductDetailsIntent.OnViewCartClick) },
                onDismissClick = { viewModel.onIntent(ProductDetailsIntent.OnDismissCartConfirmation) }
            )
        }
    }
}

@Composable
fun CartConfirmationBar(
    quantity: Int,
    onViewCartClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.surfaceVariant, Theme.shapes.medium)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "✓",
                    style = Theme.typography.title,
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primary
                )
                Column {
                    Text(
                        text = stringResource(Res.string.product_details_added_to_cart),
                        style = Theme.typography.title,
                        fontWeight = FontWeight.Bold,
                        color = Theme.colors.primaryFont
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(Res.string.product_details_quantity_in_cart),
                            style = Theme.typography.body.small,
                            color = Theme.colors.secondaryFont
                        )
                        AnimatedContent(
                            targetState = quantity,
                            label = "quantityAnimation"
                        ) { targetCount ->
                            Text(
                                text = targetCount.toString(),
                                style = Theme.typography.body.medium,
                                fontWeight = FontWeight.Bold,
                                color = Theme.colors.primaryFont
                            )
                        }
                    }
                }
            }
            TextButton(onClick = onDismissClick) {
                Text(
                    text = stringResource(Res.string.cart_checkout),
                    style = Theme.typography.body.medium,
                    color = Theme.colors.secondaryFont
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            caption = stringResource(Res.string.product_details_view_cart),
            onClick = onViewCartClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ProductDetailsScreenContent(
    uiState: ProductDetailUiState,
    onBackClick: () -> Unit,
    onAddToCart: () -> Unit,
    onOptionSelected: (String, String) -> Unit,
    onFavoriteClick: () -> Unit,
    onSeeAllReviews: () -> Unit,
    onWriteReview: () -> Unit,
    onSizeGuide: () -> Unit,
    backEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    var isDescriptionExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ProductImageCarousel(
                    imageUrls = uiState.images,
                    isFavorite = uiState.isFavorite,
                    onFavoriteClick = onFavoriteClick,
                    onBackClick = onBackClick,
                    backEnabled = backEnabled
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
                        fontWeight = FontWeight.SemiBold,
                        color = Theme.colors.primaryFont,
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = formatPrice(uiState.displayPrice),
                            style = Theme.typography.title,
                            fontWeight = FontWeight.Bold,
                            color = Theme.colors.primary,
                        )
                        StarRatingRow(
                            rating = uiState.rating.toFloat(),
                            reviewCount = uiState.reviewCount,
                        )
                    }

                    uiState.displayOptions.forEach { option ->
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Theme.colors.disable, thickness = 1.dp)
                        Spacer(Modifier.height(16.dp))

                        if (option.name.contains("Color", ignoreCase = true)) {
                            SectionHeaderRow(title = stringResource(Res.string.product_details_color_label, uiState.selectedOptions[option.name] ?: ""))
                            Spacer(Modifier.height(12.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                option.values.forEach { colorString ->
                                    val isSelected = uiState.selectedOptions[option.name] == colorString
                                    val parsedColor = colorString.toColorOrGray()

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) Theme.colors.primaryFont else Theme.colors.disable,
                                                shape = CircleShape
                                            )
                                            .padding(if (isSelected) 3.dp else 0.dp)
                                            .clip(CircleShape)
                                            .background(parsedColor)
                                            .clickable { onOptionSelected(option.name, colorString) }
                                    )
                                }
                            }
                        } else {
                            if (option.name == "Size") {
                                SectionHeaderRow(
                                    title = stringResource(Res.string.product_details_size),
                                    onActionClick = onSizeGuide
                                )
                            } else {
                                SectionHeaderRow(title = option.name)
                            }
                            Spacer(Modifier.height(12.dp))
                            OptionSelectorRow(
                                values = option.values,
                                selectedValue = uiState.selectedOptions[option.name],
                                onValueSelected = { value -> onOptionSelected(option.name, value) },
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Theme.colors.disable, thickness = 1.dp)
                    Spacer(Modifier.height(16.dp))

                    SectionHeaderRow(title = stringResource(Res.string.product_details_description))
                    Spacer(Modifier.height(12.dp))

                    Column(modifier = Modifier.animateContentSize()) {
                        Text(
                            text = uiState.description.stripHtml(),
                            style = Theme.typography.body.medium,
                            color = Theme.colors.secondaryFont,
                            lineHeight = 22.sp,
                            maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        if (!isDescriptionExpanded) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "... " + stringResource(Res.string.product_details_read_more),
                                style = Theme.typography.body.small,
                                color = Theme.colors.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.clickable { isDescriptionExpanded = true }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Theme.colors.disable, thickness = 1.dp)
                    Spacer(Modifier.height(16.dp))

                    Spacer(Modifier.height(16.dp))
                    CustomerReviewsSection(
                        reviews = uiState.reviews,
                        isLoading = uiState.reviewsLoading,
                        onSeeAllClick = onSeeAllReviews,
                        onWriteReviewClick = onWriteReview,
                    )

                    Spacer(Modifier.height(40.dp))
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Theme.colors.surface)
                .navigationBarsPadding()
        ) {
            HorizontalDivider(color = Theme.colors.disable, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AddToCartButton(
                    onAddToCart = onAddToCart,
                    enabled = uiState.canAddToCart,
                    modifier = Modifier.weight(1f)
                )

            }
        }
    }
}

fun String.toColorOrGray(): Color {
    return try {
        val cleanedInput = this.trim().lowercase()

        when {
            cleanedInput.startsWith("bla") -> return Color(0xFF1A1A1A)
            cleanedInput.startsWith("whi") -> return Color(0xFFFFFFFF)
            cleanedInput.startsWith("nav") -> return Color(0xFF1D3557)
            cleanedInput.startsWith("red") -> return Color(0xFFD32F2F)
            cleanedInput.startsWith("oli") -> return Color(0xFF4A5D4E)
            cleanedInput.startsWith("gre") || cleanedInput.startsWith("gra") -> return Color(0xFF888888)
            cleanedInput.startsWith("bei") -> return Color(0xFFE6D5BC)
            cleanedInput.startsWith("pin") -> return Color(0xFFE8A7A1)
            cleanedInput.startsWith("bro") -> return Color(0xFF6D4C41)
            cleanedInput.startsWith("yel") -> return Color(0xFFFBC02D)
            cleanedInput.startsWith("pur") -> return Color(0xFF7B1FA2)
            cleanedInput.startsWith("ora") -> return Color(0xFFF57C00)
            cleanedInput.startsWith("khi") || cleanedInput.startsWith("kha") -> return Color(0xFFC3B091)
        }

        val hexString = cleanedInput.replace("#", "")
        when (hexString.length) {
            6 -> Color("FF$hexString".toLong(16))
            8 -> Color(hexString.toLong(16))
            else -> Color(0xFFECECEC)
        }
    } catch (_: Exception) {
        Color(0xFFECECEC)
    }
}