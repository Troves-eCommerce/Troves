package com.troves.presintation.ui.cart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.dialog.LoginRequiredDialog
import com.troves.designsystem.components.dialog.TrovesDialog
import com.troves.designsystem.components.emptystate.EmptyState
import com.troves.designsystem.components.toast.TrovesSnackbarHost
import com.troves.designsystem.components.topbar.BaseTopAppBar
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.formatPrice
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.cart.components.CartItemCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.cart_empty_desc
import troves.designsystem.generated.resources.cart_empty_title
import troves.designsystem.generated.resources.ic_arrow_back
import troves.designsystem.generated.resources.*
import troves.presintation.generated.resources.Res as ResP
import troves.presintation.generated.resources.cart_remove_item_confirm

@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToProductDetails: (String) -> Unit,
    viewModel: CartViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showLoginRequiredDialog by remember { mutableStateOf(false) }
    var itemToRemove by remember { mutableStateOf<CartLineUi?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            CartEffect.NavigateBack -> onNavigateBack()
            CartEffect.NavigateToCheckout -> onNavigateToCheckout()
            CartEffect.ShowLoginRequiredDialog -> showLoginRequiredDialog = true
            is CartEffect.ShowToast -> scope.launch { snackBarHostState.showSnackbar(effect.message) }
            is CartEffect.ShowRemoveConfirmationDialog -> itemToRemove = effect.item
            CartEffect.ShowClearCartConfirmationDialog -> showClearConfirm = true
            is CartEffect.NavigateToProductDetails -> onNavigateToProductDetails(effect.productId)
        }
    }

    if (showLoginRequiredDialog) {
        LoginRequiredDialog(
            message = "You need to be logged in to manage your cart.",
            onLoginClick = {
                showLoginRequiredDialog = false
                onNavigateToLogin()
            },
            onDismiss = {
                showLoginRequiredDialog = false
            }
        )
    }

    itemToRemove?.let { item ->
        TrovesDialog(
            title = stringResource(Res.string.cart_remove_title),
            message = stringResource(ResP.string.cart_remove_item_confirm, item.title),
            confirmText = stringResource(Res.string.wishlist_remove),
            onConfirm = {
                viewModel.onIntent(CartIntent.OnRemoveItemConfirm(item.lineId))
                itemToRemove = null
            },
            onDismiss = {
                itemToRemove = null
            }
        )
    }

    if (showClearConfirm) {
        TrovesDialog(
            title = stringResource(Res.string.cart_clear_title),
            message = stringResource(Res.string.cart_clear_msg),
            confirmText = stringResource(Res.string.clear_all),
            onConfirm = {
                viewModel.onIntent(CartIntent.OnClearCartConfirm)
                showClearConfirm = false
            },
            onDismiss = { showClearConfirm = false },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CartScreenContent(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.fillMaxSize(),
        )

        TrovesSnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(16.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartScreenContent(
    state: CartUiState,
    onIntent: (CartIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Theme.colors.backGround)
            .statusBarsPadding(),
    ) {
        BaseTopAppBar(
            title = stringResource(Res.string.cart_title),
            leadingIcon = painterResource(Res.drawable.ic_arrow_back),
            onLeadingClick = { onIntent(CartIntent.OnBackClick) },
            modifier = Modifier.background(Theme.colors.backGround),
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        ) {
            if (!state.isEmpty && !state.isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BasicText(
                            text = stringResource(Res.string.cart_items_count, state.items.size.toString()),
                            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                        )
                        TextButton(onClick = { onIntent(CartIntent.OnClearCartClick) }) {
                            Text(
                                text = stringResource(Res.string.clear_all),
                                style = Theme.typography.body.medium,
                                color = Theme.colors.error,
                            )
                        }
                    }
                }
            }

            itemsIndexed(state.items, key = { _, item -> item.lineId }) { index, item ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            onIntent(CartIntent.OnRemoveItemClick(item.lineId))
                            false
                        } else {
                            false
                        }
                    }
                )

                val haptic = LocalHapticFeedback.current
                // One-time nudge on the first row so users discover the swipe-to-delete gesture.
                // hintOffsetX drives the card's translation; hintProgress derives how much of the
                // red "delete" backdrop should be revealed behind it as it slides.
                val hintOffsetX = remember { Animatable(0f) }
                val hintProgress by remember { derivedStateOf { (-hintOffsetX.value / 90f).coerceIn(0f, 1f) } }

                LaunchedEffect(state.shouldShowCartHint) {
                    if (index == 0 && state.shouldShowCartHint) {
                        delay(500)
                        // Ease out into the peek — slower start, quick reveal — then hold a beat
                        // so the eye actually registers the red "delete" backdrop.
                        hintOffsetX.animateTo(
                            targetValue = -90f,
                            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
                        )
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        delay(500)
                        // Snap back with a gentle spring instead of a linear tween — reads as the
                        // card "settling" back into place rather than mechanically resetting.
                        hintOffsetX.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                        )
                        onIntent(CartIntent.OnDismissCartHint)
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    // Peek backdrop: identical to the real swipe backgroundContent below (solid
                    // red, full size, "remove" label) — only visible while the hint animation
                    // runs, so it teaches exactly what swiping reveals.
                    if (hintProgress > 0f) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(Theme.shapes.large)
                                .background(Theme.colors.error)
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            Text(
                                text = stringResource(Res.string.wishlist_remove),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                Theme.colors.error
                            } else {
                                Color.Transparent
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(Theme.shapes.large)
                                    .background(color)
                                    .padding(horizontal = 24.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                    Text(
                                        text = stringResource(Res.string.wishlist_remove),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        enableDismissFromStartToEnd = false,
                    ) {
                        CartItemCard(
                            item = item,
                            onIncrement = { onIntent(CartIntent.OnIncrement(item.lineId)) },
                            onDecrement = { onIntent(CartIntent.OnDecrement(item.lineId)) },
                            onClick = { onIntent(CartIntent.OnItemClick(item.lineId)) },
                            modifier = Modifier.graphicsLayer {
                                translationX = hintOffsetX.value
                                // A hair of scale-down while peeking makes the card feel like it's
                                // lifting off the row rather than just sliding sideways.
                                val liftScale = 1f - hintProgress * 0.02f
                                scaleX = liftScale
                                scaleY = liftScale
                            },
                        )
                    }
                }
            }

            if (state.isEmpty) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        EmptyState(
                            title = stringResource(Res.string.cart_empty_title),
                            description = stringResource(Res.string.cart_empty_desc),
                            icon = Icons.Default.ShoppingCart,
                        )
                    }
                }
            }
        }
        if (!state.isEmpty) {
            CartBottomBar(
                totalFormatted = formatPrice(state.total?.amount ?: "0.00"),
                onCheckout = { onIntent(CartIntent.OnCheckout) },
                isLoading = state.isLoading,
                isEmpty = state.isEmpty,
            )
        }
    }
}

@Composable
private fun CartBottomBar(
    totalFormatted: String,
    onCheckout: () -> Unit,
    isLoading: Boolean,
    isEmpty: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp)
            .background(Theme.colors.backGround)
            .navigationBarsPadding()
            .padding(
                horizontal = Theme.spacing.medium,
                vertical = Theme.spacing.medium,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)) {
            BasicText(
                text = stringResource(Res.string.cart_total),
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
            )
            BasicText(
                text = totalFormatted,
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        PrimaryButton(
            caption = stringResource(Res.string.cart_checkout),
            onClick = onCheckout,
            isDisabled = isLoading || isEmpty,
            modifier = Modifier
                .weight(1f)
                .padding(start = Theme.spacing.medium),
        )
    }
}