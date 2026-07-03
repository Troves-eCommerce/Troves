package com.troves.presintation.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.button.SecondaryButton
import com.troves.designsystem.components.topbar.BaseTopAppBar
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back

@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onOrderPlaced: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    viewModel: CheckoutViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.onIntent(CheckoutIntent.OnResume)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            CheckoutEffect.NavigateBack -> onNavigateBack()
            CheckoutEffect.NavigateToAddresses -> onNavigateToAddresses()
            is CheckoutEffect.OrderPlaced -> {
                scope.launch { snackBarHostState.showSnackbar("Order ${effect.orderName} placed") }
                onOrderPlaced()
            }
            is CheckoutEffect.OpenCheckoutUrl -> uriHandler.openUri(effect.url)
            is CheckoutEffect.ShowToast -> scope.launch { snackBarHostState.showSnackbar(effect.message) }
            CheckoutEffect.ShowLoginRequiredDialog -> onNavigateToLogin()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            containerColor = Theme.colors.backGround,
            topBar = {
                BaseTopAppBar(
                    title = "Checkout",
                    leadingIcon = painterResource(Res.drawable.ic_arrow_back),
                    onLeadingClick = { viewModel.onIntent(CheckoutIntent.OnBack) },
                    modifier = Modifier.background(Theme.colors.backGround),
                )
            },
            bottomBar = {
                CheckoutActions(
                    state = state,
                    onPlaceCod = { viewModel.onIntent(CheckoutIntent.OnPlaceCodOrder) },
                    onPayByCard = { viewModel.onIntent(CheckoutIntent.OnPayByCard) },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
            ) {
                SectionCard(title = "Shipping address") {
                    if (state.hasAddress) {
                        if (state.recipientName.isNotBlank()) {
                            BasicText(
                                text = state.recipientName,
                                style = Theme.typography.body.large.copy(
                                    color = Theme.colors.primaryFont,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            )
                        }
                        BasicText(
                            text = state.addressLine,
                            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                        )
                    } else if (!state.isLoading) {
                        BasicText(
                            text = "Sorry you don't have an address to deliver to",
                            style = Theme.typography.body.medium.copy(color = Theme.colors.error),
                        )
                    }
                    SecondaryButton(
                        caption = if (state.hasAddress) "Change address" else "Add address",
                        onClick = { viewModel.onIntent(CheckoutIntent.OnManageAddress) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                SectionCard(title = "Order summary") {
                    SummaryRow("Items", state.itemCount.toString())
                    SummaryRow("Subtotal", state.subtotalFormatted)
                    SummaryRow("Total", state.totalFormatted, emphasize = true)
                }
            }
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(16.dp),
        )
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        BasicText(
            text = title,
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
        content()
    }
}

@Composable
private fun SummaryRow(label: String, value: String, emphasize: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        BasicText(
            text = label,
            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
        )
        BasicText(
            text = value,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Medium,
            ),
        )
    }
}

@Composable
private fun CheckoutActions(
    state: CheckoutUiState,
    onPlaceCod: () -> Unit,
    onPayByCard: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .navigationBarsPadding()
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        PrimaryButton(
            caption = if (state.isPlacingOrder) "Placing order..." else "Cash on Delivery",
            onClick = onPlaceCod,
            isDisabled = !state.canPlaceOrder,
            modifier = Modifier.fillMaxWidth(),
        )
        PrimaryButton(
            caption = "Pay by Card",
            onClick = onPayByCard,
            isDisabled = state.isPlacingOrder || state.isCartEmpty || !state.hasAddress,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
