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
import androidx.compose.material3.OutlinedTextField
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
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.*

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
            message = stringResource(Res.string.cart_login_msg),
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
            message = stringResource(Res.string.cart_remove_msg),
            confirmText = stringResource(Res.string.cart_remove_title), // Should be "Remove"
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
                title = stringResource(Res.string.cart_title),
                leadingIcon = painterResource(Res.drawable.ic_arrow_back),
                onLeadingClick = { onIntent(CartIntent.OnBackClick) },
                modifier = Modifier.background(Theme.colors.backGround),
                autoMirrorLeadingIcon = true
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
                            text = stringResource(Res.string.cart_items_count, state.items.size),
                            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                        )
                        BasicText(
                            text = stringResource(Res.string.clear_all),
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
                            text = stringResource(Res.string.cart_empty),
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
private fun DiscountCodeRow(
    value: String,
    appliedCode: String?,
    isApplying: Boolean,
    onValueChange: (String) -> Unit,
    onApply: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                placeholder = { BasicText(stringResource(Res.string.cart_discount_code)) },
                modifier = Modifier.weight(1f),
            )
            PrimaryButton(
                caption = if (isApplying) "..." else stringResource(Res.string.apply),
                onClick = onApply,
                isDisabled = isApplying || value.isBlank(),
            )
        }
        if (!appliedCode.isNullOrBlank()) {
            BasicText(
                text = "Applied: $appliedCode",
                style = Theme.typography.body.small.copy(color = Theme.colors.primary),
            )
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
            isDisabled = isLoading,
            modifier = Modifier
                .weight(1f)
                .padding(start = Theme.spacing.medium),
        )
    }
}
