package com.troves.presintation.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.theme.Theme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        containerColor = Theme.colors.backGround,
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.isEmpty -> BasicText(
                    text = "You have no orders yet",
                    style = Theme.typography.body.large.copy(color = Theme.colors.secondaryFont),
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Theme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                ) {
                    item {
                        BasicText(
                            text = "My Orders",
                            style = Theme.typography.title.copy(
                                color = Theme.colors.primaryFont,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                    items(state.orders, key = { it.id }) { order -> OrderCard(order) }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: OrderUi) {
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
        ) {
            BasicText(
                text = order.name,
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )
            BasicText(
                text = order.totalFormatted,
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
        BasicText(
            text = order.date,
            style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
        )
        if (order.status.isNotBlank()) {
            BasicText(
                text = order.status,
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
            )
        }
    }
}
