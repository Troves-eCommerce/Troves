package com.troves.presintation.ui.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.troves.designsystem.components.chip.OrderStatusBadge
import com.troves.designsystem.components.shimmer.shimmerEffect
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_chevron_right
import troves.designsystem.generated.resources.img_placeholder
import troves.designsystem.generated.resources.orders_empty_title
import troves.designsystem.generated.resources.orders_empty_desc
import troves.designsystem.generated.resources.orders_title
import org.jetbrains.compose.resources.stringResource
import com.troves.designsystem.components.emptystate.EmptyState
import com.troves.presintation.ui.components.SignInRequiredState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import troves.designsystem.generated.resources.orders_auth_required_desc

@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = koinViewModel(),
    onNavigateToDetails: (String) -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OrdersEffect.NavigateToOrderDetails -> onNavigateToDetails(effect.id)
                OrdersEffect.NavigateBack -> { /* Handle if needed */ }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(OrdersIntent.Load)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding(),
    ) {
        when {
                state.isNotSignedIn -> SignInRequiredState(
                    description = stringResource(Res.string.orders_auth_required_desc),
                    onSignIn = onNavigateToRegister,
                )

                state.isLoading -> OrdersLoadingContent()

                state.isError -> BasicText(
                    text = state.errorMessage ?: "Failed to load orders",
                    style = Theme.typography.body.large.copy(color = Theme.colors.error),
                    modifier = Modifier.align(Alignment.Center),
                )

                state.isEmpty -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        title = stringResource(Res.string.orders_empty_title),
                        description = stringResource(Res.string.orders_empty_desc),
                        icon = Icons.Default.List,
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Theme.spacing.medium,
                        end = Theme.spacing.medium,
                        top = Theme.spacing.medium,
                        bottom = 100.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                ) {
                    item {
                        BasicText(
                            text = stringResource(Res.string.orders_title),
                            style = Theme.typography.title.copy(
                                color = Theme.colors.primaryFont,
                                fontWeight = FontWeight.Bold,
                            ),
                            modifier = Modifier.padding(bottom = Theme.spacing.small)
                        )
                    }
                    items(state.orders, key = { it.id }) { order -> 
                        OrderCard(
                            order = order,
                            onClick = { viewModel.onIntent(OrdersIntent.OnOrderClick(order.id)) }
                        ) 
                    }
                }
            }
        }
    }

@Composable
private fun OrdersLoadingContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Theme.spacing.medium,
            end = Theme.spacing.medium,
            top = Theme.spacing.medium,
            bottom = 100.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        item {
            Box(
                modifier = Modifier
                    .padding(bottom = Theme.spacing.small)
                    .width(140.dp)
                    .height(28.dp)
                    .clip(Theme.shapes.small)
                    .shimmerEffect()
            )
        }
        items(5) {
            OrderCardSkeleton()
        }
    }
}

@Composable
private fun OrderCardSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.onPrimary.copy(alpha = 0.5f), Theme.shapes.large)
            .padding(Theme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(Theme.shapes.medium)
                    .shimmerEffect()
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(24.dp)
                            .clip(Theme.shapes.small)
                            .shimmerEffect()
                    )
                }
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .shimmerEffect()
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun OrderCard(order: OrderUi, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.onPrimary.copy(alpha = 0.5f), Theme.shapes.large)
            .clickable(onClick = onClick)
            .padding(Theme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (order.imageUrl != null) {
                AsyncImage(
                    model = order.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(Theme.shapes.medium)
                        .background(Theme.colors.surfaceVariant)
                )
            } else {
                Image(
                    painter = painterResource(Res.drawable.img_placeholder),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(Theme.shapes.medium)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicText(
                        text = "Order #${order.name}",
                        style = Theme.typography.body.large.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    
                    if (order.status.isNotBlank()) {
                        OrderStatusBadge(status = order.status)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicText(
                        text = order.date,
                        style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
                    )
                    BasicText(
                        text = "  ·  ${order.itemCount} items",
                        style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                text = order.totalFormatted,
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )
            
            Icon(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = "View Details",
                tint = Theme.colors.primaryFont,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
