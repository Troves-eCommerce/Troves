package com.troves.presintation.ui.fav.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Product
import com.troves.presintation.ui.fav.WishlistIntent
import com.troves.presintation.ui.fav.WishlistState
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_wishlist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistContent(
    state: WishlistState,
    onIntent: (WishlistIntent) -> Unit,
    onRemoveClick: (Product) -> Unit,
    onClearAllClick: () -> Unit,
) {

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { onIntent(WishlistIntent.Refresh) },
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            WishlistHeader(
                itemCount = state.items.size,
                showClearButton = state.items.isNotEmpty(),
                onClearAllClick = onClearAllClick
            )

            when {
                state.errorMessage != null -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = state.errorMessage,
                            style = Theme.typography.body.medium,
                            color = Theme.colors.error
                        )
                    }
                }

                state.items.isEmpty() -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_wishlist),
                                contentDescription = null,
                                tint = Theme.colors.primary,
                                modifier = Modifier.size(80.dp)
                            )
                            Text(
                                text = "Your wishlist is empty",
                                style = Theme.typography.body.large.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Theme.colors.secondaryFont
                            )
                        }
                    }
                }

                else -> {

                    WishlistGrid(
                        products = state.items,
                        onProductClick = {
                            onIntent(WishlistIntent.ProductClicked(it))
                        },
                        onRemoveClick = onRemoveClick,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
