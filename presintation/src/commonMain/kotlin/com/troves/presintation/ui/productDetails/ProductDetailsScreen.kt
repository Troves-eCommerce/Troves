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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.formatPrice
import com.troves.designsystem.util.stripHtml
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.productDetails.components.AddToCartButton
import com.troves.presintation.ui.productDetails.components.CustomerReviewsSection
import com.troves.presintation.ui.productDetails.components.OptionSelectorRow
import com.troves.presintation.ui.productDetails.components.ProductDetailsShimmer
import com.troves.presintation.ui.productDetails.components.ProductImageCarousel
import com.troves.presintation.ui.productDetails.components.SectionHeaderRow
import com.troves.presintation.ui.productDetails.components.StarRatingRow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_cart
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
        LoginRequiredDialog(
            message = "You need to be logged in to manage your favorites.",
            onLoginClick = {
                showLoginRequiredDialog = false
                onNavigateToLogin()
            },
            onDismiss = { showLoginRequiredDialog = false }
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
                    onFavoriteClick = { intent(ProductDetailsIntent.OnFavoriteClick) },
                    onSeeAllReviews = { intent(ProductDetailsIntent.OnSeeAllReviews) },
                    onSizeGuide = { intent(ProductDetailsIntent.OnSizeGuide) },
                    modifier = Modifier.fillMaxSize(),
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
    onOptionSelected: (String, String) -> Unit,
    onFavoriteClick: () -> Unit,
    onSeeAllReviews: () -> Unit,
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
                        color = Color(0xFF1A1A1A),
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = formatPrice(uiState.displayPrice),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A5D4E),
                        )
                        StarRatingRow(
                            rating = uiState.rating.toFloat(),
                            reviewCount = uiState.reviewCount,
                        )
                    }

                    uiState.displayOptions.forEach { option ->
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFECECEC), thickness = 1.dp)
                        Spacer(Modifier.height(16.dp))

                        if (option.name.contains("Color", ignoreCase = true)) {
                            SectionHeaderRow(title = "Color: ${uiState.selectedOptions[option.name] ?: ""}")
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
                                                color = if (isSelected) Color.Black else Color(0xFFE2E2E2),
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
                                    title = option.name,
                                    actionLabel = "Size Guide",
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

                    // قسم الوصف (Description)
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFECECEC), thickness = 1.dp)
                    Spacer(Modifier.height(16.dp))

                    SectionHeaderRow(title = "Description")
                    Spacer(Modifier.height(10.dp))

                    Column(modifier = Modifier.animateContentSize()) {
                        Text(
                            text = uiState.description.stripHtml(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            lineHeight = 22.sp,
                            maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = if (isDescriptionExpanded) "Read less ∧" else "Read more ∨",
                            color = Color(0xFF4A5D4E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { isDescriptionExpanded = !isDescriptionExpanded }
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFECECEC), thickness = 1.dp)
                    Spacer(Modifier.height(16.dp))

                    if (uiState.reviews.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFECECEC), thickness = 1.dp)
                        Spacer(Modifier.height(16.dp))

                        CustomerReviewsSection(
                            reviews = uiState.reviews,
                            onSeeAllClick = onSeeAllReviews,
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        // 2. البار السفلي الثابت النظيف تماماً (بدون السعر وبدون الفاليديتورز التحذيرية المزعجة)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
        ) {
            HorizontalDivider(color = Color.Black.copy(alpha = 0.06f), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AddToCartButton(
                    onAddToCart = onAddToCart,
                    enabled = true,
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
            cleanedInput.startsWith("bla") -> return Color(0xFF1A1A1A)     // أسود داكن
            cleanedInput.startsWith("whi") -> return Color(0xFFFFFFFF)     // أبيض صافي
            cleanedInput.startsWith("nav") -> return Color(0xFF1D3557)     // كحلي
            cleanedInput.startsWith("red") -> return Color(0xFFD32F2F)     // أحمر
            cleanedInput.startsWith("oli") -> return Color(0xFF4A5D4E)     // زيتوني (براند التطبيق)
            cleanedInput.startsWith("gre") || cleanedInput.startsWith("gra") -> return Color(0xFF888888) // رمادي
            cleanedInput.startsWith("bei") -> return Color(0xFFE6D5BC)     // بيج دافئ
            cleanedInput.startsWith("pin") -> return Color(0xFFE8A7A1)     // وردي ناعم
            cleanedInput.startsWith("bro") -> return Color(0xFF6D4C41)     // بني
            cleanedInput.startsWith("yel") -> return Color(0xFFFBC02D)     // أصفر دافئ
            cleanedInput.startsWith("pur") -> return Color(0xFF7B1FA2)     // بنفسجي
            cleanedInput.startsWith("ora") -> return Color(0xFFF57C00)     // برتقالي
            cleanedInput.startsWith("khi") || cleanedInput.startsWith("kha") -> return Color(0xFFC3B091) // كاكي
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