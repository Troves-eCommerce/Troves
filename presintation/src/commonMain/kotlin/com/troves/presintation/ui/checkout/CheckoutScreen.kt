package com.troves.presintation.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.button.SecondaryButton
import com.troves.designsystem.components.topbar.BaseTopAppBar
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Address
import com.troves.presintation.core.mvi.ObserveEffect
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back
import troves.designsystem.generated.resources.ic_home
import troves.designsystem.generated.resources.ic_profile
import com.troves.domain.entity.AddressIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToOrderSuccess: () -> Unit,
    onNavigateToNewAddress: () -> Unit,
    viewModel: CheckoutViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            CheckoutEffect.NavigateBack -> onNavigateBack()
            CheckoutEffect.NavigateToOrderSuccess -> onNavigateToOrderSuccess()
            CheckoutEffect.NavigateToNewAddress -> onNavigateToNewAddress()
        }
    }

    Scaffold(
        containerColor = Theme.colors.backGround,
        topBar = {
            BaseTopAppBar(
                title = "Checkout",
                leadingIcon = painterResource(Res.drawable.ic_arrow_back),
                onLeadingClick = { viewModel.onIntent(CheckoutIntent.OnBackClick) },
                modifier = Modifier.background(Theme.colors.backGround),
            )
        },
        bottomBar = {
            CheckoutBottomBar(
                state = state,
                onPlaceOrder = { viewModel.onIntent(CheckoutIntent.OnPlaceOrder) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.large)
        ) {
            // Address Section
            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shipping Address",
                        style = Theme.typography.body.large.copy(fontWeight = FontWeight.Bold),
                        color = Theme.colors.primaryFont
                    )
                    Text(
                        text = "Change",
                        style = Theme.typography.body.medium,
                        color = Theme.colors.primary,
                        modifier = Modifier.clickable { viewModel.onIntent(CheckoutIntent.OnChangeAddressClick) }
                    )
                }

                if (state.selectedAddress != null) {
                    SelectedAddressCard(address = state.selectedAddress!!)
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Theme.colors.surface)
                            .padding(Theme.spacing.medium),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No address selected.",
                            style = Theme.typography.body.medium,
                            color = Theme.colors.secondaryFont
                        )
                    }
                }
            }

            // Order Summary Section
            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                Text(
                    text = "Order Summary",
                    style = Theme.typography.body.large.copy(fontWeight = FontWeight.Bold),
                    color = Theme.colors.primaryFont
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Theme.colors.surface)
                        .padding(Theme.spacing.medium)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", style = Theme.typography.body.medium, color = Theme.colors.secondaryFont)
                            Text(state.subtotalFormatted, style = Theme.typography.body.medium.copy(fontWeight = FontWeight.Medium), color = Theme.colors.primaryFont)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Shipping", style = Theme.typography.body.medium, color = Theme.colors.secondaryFont)
                            Text(state.shippingFormatted, style = Theme.typography.body.medium.copy(fontWeight = FontWeight.Medium), color = Theme.colors.primaryFont)
                        }
                        Divider(color = Theme.colors.hint.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", style = Theme.typography.body.large.copy(fontWeight = FontWeight.Bold), color = Theme.colors.primaryFont)
                            Text(state.totalFormatted, style = Theme.typography.body.large.copy(fontWeight = FontWeight.Bold), color = Theme.colors.primaryFont)
                        }
                    }
                }
            }
        }
    }

    if (state.showAddressSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onIntent(CheckoutIntent.OnDismissAddressSheet) },
            containerColor = Theme.colors.backGround
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
            ) {
                Text(
                    text = "Select Address",
                    style = Theme.typography.title.copy(fontWeight = FontWeight.Bold),
                    color = Theme.colors.primaryFont
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                    items(state.addresses, key = { it.id }) { address ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Theme.colors.surface)
                                .clickable { viewModel.onIntent(CheckoutIntent.OnAddressSelected(address)) }
                                .padding(Theme.spacing.medium)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    painter = if (address.icon == AddressIcon.HOME) painterResource(Res.drawable.ic_home) else painterResource(Res.drawable.ic_profile),
                                    contentDescription = null,
                                    tint = Theme.colors.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = address.label,
                                        style = Theme.typography.body.large.copy(fontWeight = FontWeight.Medium),
                                        color = Theme.colors.primaryFont
                                    )
                                    Text(
                                        text = address.lines.joinToString(", "),
                                        style = Theme.typography.body.medium,
                                        color = Theme.colors.secondaryFont
                                    )
                                }
                            }
                        }
                    }
                }
                
                SecondaryButton(
                    caption = "Add New Address",
                    onClick = { viewModel.onIntent(CheckoutIntent.OnAddNewAddress) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Theme.spacing.large))
            }
        }
    }
}

@Composable
private fun SelectedAddressCard(address: Address) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.colors.surface)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Theme.colors.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = if (address.icon == AddressIcon.HOME) painterResource(Res.drawable.ic_home) else painterResource(Res.drawable.ic_profile),
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = address.label,
                style = Theme.typography.body.large.copy(fontWeight = FontWeight.SemiBold),
                color = Theme.colors.primaryFont
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = address.lines.joinToString(", "),
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = address.phone,
                style = Theme.typography.body.small,
                color = Theme.colors.secondaryFont
            )
        }
    }
}

@Composable
private fun CheckoutBottomBar(
    state: CheckoutUiState,
    onPlaceOrder: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .navigationBarsPadding()
            .padding(Theme.spacing.medium)
    ) {
        PrimaryButton(
            caption = "Place Order",
            onClick = onPlaceOrder,
            isDisabled = state.selectedAddress == null || state.isPlacingOrder || state.cartItems.isEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
