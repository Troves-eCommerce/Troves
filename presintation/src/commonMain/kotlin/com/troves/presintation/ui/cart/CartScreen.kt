package com.troves.presintation.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.dialog.LoginRequiredDialog
import com.troves.designsystem.components.dialog.TrovesDialog
import com.troves.designsystem.components.topbar.BaseTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.cart.components.CartItemCard
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back

@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToLogin: () -> Unit,
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
            title = "Remove Item",
            message = "Are you sure you want to remove \"${item.title}\" from your cart?",
            confirmText = "Remove",
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
            title = "Clear Cart",
            message = "Remove all items from your cart?",
            confirmText = "Clear All",
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
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        )

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
private fun CartScreenContent(
    state: CartUiState,
    onIntent: (CartIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Theme.colors.backGround,
        topBar = {
            BaseTopAppBar(
                title = "Cart",
                leadingIcon = painterResource(Res.drawable.ic_arrow_back),
                onLeadingClick = { onIntent(CartIntent.OnBackClick) },
                modifier = Modifier.background(Theme.colors.backGround),
            )
        },
        bottomBar = {
            CartBottomBar(
                totalFormatted = state.totalFormatted,
                onCheckout = { onIntent(CartIntent.OnCheckout) },
                isLoading = state.isLoading,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                            text = "${state.items.size} item(s)",
                            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                        )
                        BasicText(
                            text = "Clear all",
                            style = Theme.typography.body.medium.copy(color = Theme.colors.error),
                            modifier = Modifier.clickable { onIntent(CartIntent.OnClearCartClick) },
                        )
                    }
                }
            }

            items(state.items, key = { it.lineId }) { item ->
                CartItemCard(
                    item = item,
                    onIncrement = { onIntent(CartIntent.OnIncrement(item.lineId)) },
                    onDecrement = { onIntent(CartIntent.OnDecrement(item.lineId)) },
                    onRemove = { onIntent(CartIntent.OnRemoveItemClick(item.lineId)) },
                )
            }

            if (state.isEmpty) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Theme.spacing.extraLarge),
                        contentAlignment = Alignment.Center,
                    ) {
                        BasicText(
                            text = "Your cart is empty",
                            style = Theme.typography.body.large.copy(
                                color = Theme.colors.secondaryFont,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartBottomBar(
    totalFormatted: String,
    onCheckout: () -> Unit,
    isLoading: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                text = "Total Price",
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
            caption = "Checkout",
            onClick = onCheckout,
            isDisabled = isLoading,
            modifier = Modifier
                .weight(1f)
                .padding(start = Theme.spacing.medium),
        )
    }
}
