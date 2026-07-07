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
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.troves.designsystem.components.cards.OrderSummaryInfoCard
import com.troves.designsystem.components.cards.OrderSummaryItemCard
import com.troves.designsystem.components.chip.OrderStatusBadge
import com.troves.designsystem.components.topbar.BaseTopAppBar
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.orderdetails.components.OrderTimeline
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back
import troves.designsystem.generated.resources.img_placeholder

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

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        containerColor = Theme.colors.backGround,
        topBar = {
            BaseTopAppBar(
                title = "Order Details",
                leadingIcon = painterResource(Res.drawable.ic_arrow_back),
                onLeadingClick = { viewModel.onIntent(OrderDetailsIntent.OnBack) },
                modifier = Modifier.background(Theme.colors.backGround),
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                state.isError -> {
                    BasicText(
                        text = state.errorMessage ?: "Something went wrong",
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
                text = "Order Progress",
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
                text = "Order Items",
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
                            .fillMaxHeight()
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
                            maxLines = 2,
                        )
                        if (item.variant.isNotBlank()) {
                            BasicText(
                                text = item.variant,
                                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                            )
                        }
                        BasicText(
                            text = "Qty: ${item.quantity}",
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
                text = "Order Summary",
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(top = Theme.spacing.small, bottom = Theme.spacing.small)
            )
            
            OrderSummaryInfoCard(
                subtotalLabel = "Subtotal",
                subtotalFormatted = orderDetails.subtotal,
                shippingLabel = if (orderDetails.shipping.isNotBlank()) "Shipping" else null,
                shippingFormatted = if (orderDetails.shipping.isNotBlank()) orderDetails.shipping else null,
                taxLabel = if (orderDetails.tax.isNotBlank()) "Tax" else null,
                taxFormatted = if (orderDetails.tax.isNotBlank()) orderDetails.tax else null,
                totalLabel = "Total",
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
                    text = "Order #${orderDetails.orderNumber}",
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                BasicText(
                    text = "Placed on ${orderDetails.orderDate}",
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
                text = "${orderDetails.itemCount} items",
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
                text = "Need help with your order?",
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )
            BasicText(
                text = "Contact our support team",
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            )
        }
    }
}
