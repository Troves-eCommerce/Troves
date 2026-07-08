package com.troves.presintation.ui.orderdetails

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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.troves.presintation.ui.components.NoConnectionState
import com.troves.designsystem.components.cards.OrderSummaryInfoCard
import com.troves.designsystem.components.cards.OrderSummaryItemCard
import com.troves.designsystem.components.chip.OrderStatusBadge
import com.troves.designsystem.components.shimmer.shimmerEffect
import com.troves.designsystem.components.topbar.BaseTopAppBar
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.orderdetails.components.OrderTimeline
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back
import troves.designsystem.generated.resources.img_placeholder
import troves.presintation.generated.resources.Res as ResP
import troves.presintation.generated.resources.order_details_title
import troves.presintation.generated.resources.error_view_title
import troves.presintation.generated.resources.order_details_progress
import troves.presintation.generated.resources.order_details_items
import troves.presintation.generated.resources.checkout_order_summary
import troves.presintation.generated.resources.order_details_subtotal
import troves.presintation.generated.resources.order_details_shipping
import troves.presintation.generated.resources.order_details_tax
import troves.presintation.generated.resources.checkout_total
import troves.presintation.generated.resources.checkout_items_count
import troves.presintation.generated.resources.order_details_help
import troves.presintation.generated.resources.order_details_support
import troves.presintation.generated.resources.order_details_qty
import troves.presintation.generated.resources.order_details_order_number
import troves.presintation.generated.resources.order_details_placed_on

@Composable
fun OrderDetailsScreen(
    orderId: String,
    viewModel: OrderDetailsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToSupport: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) {
        viewModel.onIntent(OrderDetailsIntent.Load(orderId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OrderDetailsEffect.NavigateBack -> onNavigateBack()
                OrderDetailsEffect.NavigateToSupport -> onNavigateToSupport()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding(),
    ) {
        BaseTopAppBar(
            title = stringResource(ResP.string.order_details_title),
            leadingIcon = painterResource(Res.drawable.ic_arrow_back),
            onLeadingClick = { viewModel.onIntent(OrderDetailsIntent.OnBack) },
            modifier = Modifier.background(Theme.colors.backGround),
        )
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    OrderDetailsLoadingContent()
                }
                state.showOfflineState -> {
                    NoConnectionState(
                        onRetry = { viewModel.onIntent(OrderDetailsIntent.Load(orderId)) },
                    )
                }
                state.isError -> {
                    BasicText(
                        text = state.errorMessage ?: stringResource(ResP.string.error_view_title),
                        style = Theme.typography.body.large.copy(color = Theme.colors.error),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.orderDetails != null -> {
                    OrderDetailsContent(
                        orderDetails = state.orderDetails!!,
                        onSupportClick = { viewModel.onIntent(OrderDetailsIntent.OnSupportClick) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderDetailsContent(
    orderDetails: OrderDetailsUi,
    onSupportClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        item {
            OrderSummaryHeaderCard(orderDetails)
        }
        
        item {
            BasicText(
                text = stringResource(ResP.string.order_details_progress),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(vertical = Theme.spacing.small)
            )
            OrderTimeline(steps = orderDetails.timelineSteps)
        }

        item {
            BasicText(
                text = stringResource(ResP.string.order_details_items),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(vertical = Theme.spacing.small)
            )
        }

        items(orderDetails.items, key = { it.id }) { item ->
            val painter = if (item.imageUrl != null) null else painterResource(Res.drawable.img_placeholder)
            
            if (item.imageUrl != null) {
                // Async image for product
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Theme.shapes.medium)
                        .border(1.dp, Theme.colors.onPrimary.copy(alpha = 0.5f), Theme.shapes.medium)
                .padding(end = 16.dp)
                    ,
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .width(128.dp)
                            .height(128.dp)
                            .clip(Theme.shapes.medium)
                            .background(Theme.colors.surfaceVariant),
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
                    ) {
                        BasicText(
                            text = item.name,
                            style = Theme.typography.body.large.copy(
                                color = Theme.colors.primaryFont,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.variant.isNotBlank()) {
                            BasicText(
                                text = item.variant,
                                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                            )
                        }
                        BasicText(
                            text = stringResource(ResP.string.order_details_qty, item.quantity),
                            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                        )
                    }

                    BasicText(
                        text = item.price,
                        style = Theme.typography.body.large.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .padding(bottom = Theme.spacing.extraSmall),
                    )
                }
            } else {
                OrderSummaryItemCard(
                    imagePainter = painter!!,
                    name = item.name,
                    specs = item.variant,
                    quantity = item.quantity,
                    priceFormatted = item.price,
                )
            }
        }

        item {
            BasicText(
                text = stringResource(ResP.string.checkout_order_summary),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(top = Theme.spacing.small, bottom = Theme.spacing.small)
            )
            
            OrderSummaryInfoCard(
                subtotalLabel = stringResource(ResP.string.order_details_subtotal),
                subtotalFormatted = orderDetails.subtotal,
                shippingLabel = if (orderDetails.shipping.isNotBlank()) stringResource(ResP.string.order_details_shipping) else null,
                shippingFormatted = if (orderDetails.shipping.isNotBlank()) orderDetails.shipping else null,
                taxLabel = if (orderDetails.tax.isNotBlank()) stringResource(ResP.string.order_details_tax) else null,
                taxFormatted = if (orderDetails.tax.isNotBlank()) orderDetails.tax else null,
                totalLabel = stringResource(ResP.string.checkout_total),
                totalFormatted = orderDetails.total,
            )
        }

        item {
            Spacer(modifier = Modifier.height(Theme.spacing.medium))
            SupportCard(onClick = onSupportClick)
        }
    }
}

@Composable
private fun OrderDetailsLoadingContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        // Header card skeleton
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Theme.shapes.large)
                    .background(Theme.colors.surface)
                    .padding(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ShimmerLine(width = 120.dp, height = 18.dp)
                        ShimmerLine(width = 160.dp, height = 14.dp)
                    }
                    ShimmerLine(width = 90.dp, height = 24.dp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing.small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(24.dp)
                            .clip(Theme.shapes.small)
                            .shimmerEffect()
                    )
                    ShimmerLine(width = 60.dp, height = 14.dp)
                }
            }
        }

        // Section title skeleton
        item {
            ShimmerLine(
                width = 140.dp,
                height = 22.dp,
                modifier = Modifier.padding(vertical = Theme.spacing.small),
            )
        }

        // Order item skeletons
        items(3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Theme.shapes.medium)
                    .border(1.dp, Theme.colors.onPrimary.copy(alpha = 0.5f), Theme.shapes.medium)
                    .padding(Theme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(Theme.shapes.medium)
                        .shimmerEffect()
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
                ) {
                    ShimmerLine(width = 180.dp, height = 16.dp)
                    ShimmerLine(width = 100.dp, height = 14.dp)
                    ShimmerLine(width = 60.dp, height = 14.dp)
                }
                ShimmerLine(width = 60.dp, height = 16.dp)
            }
        }

        // Summary card skeleton
        item {
            ShimmerLine(
                width = 140.dp,
                height = 22.dp,
                modifier = Modifier.padding(vertical = Theme.spacing.small),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Theme.shapes.large)
                    .background(Theme.colors.surface)
                    .padding(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            ) {
                repeat(3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        ShimmerLine(width = 80.dp, height = 14.dp)
                        ShimmerLine(width = 60.dp, height = 14.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShimmerLine(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(6.dp))
            .shimmerEffect()
    )
}

@Composable
private fun OrderSummaryHeaderCard(orderDetails: OrderDetailsUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BasicText(
                    text = stringResource(ResP.string.order_details_order_number, orderDetails.orderNumber),
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                BasicText(
                    text = stringResource(ResP.string.order_details_placed_on, orderDetails.orderDate),
                    style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
                )
            }
            BasicText(
                text = orderDetails.totalAmount,
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (orderDetails.status.isNotBlank()) {
                OrderStatusBadge(status = orderDetails.status)
            }
            BasicText(
                text = stringResource(ResP.string.checkout_items_count, orderDetails.itemCount),
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            )
        }
    }
}

@Composable
private fun SupportCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surface)
            .clickable(onClick = onClick)
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BasicText(
                text = stringResource(ResP.string.order_details_help),
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )
            BasicText(
                text = stringResource(ResP.string.order_details_support),
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            )
        }
    }
}
