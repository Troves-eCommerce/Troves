package com.troves.presintation.ui.fav.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Product
import com.troves.presintation.ui.fav.WishlistIntent
import com.troves.presintation.ui.fav.WishlistState

@Composable
fun WishlistContent(
    state: WishlistState,
    onIntent: (WishlistIntent) -> Unit,
    onRemoveClick: (Product) -> Unit,
    onClearAllClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(bottom = 24.dp),
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
                    modifier = Modifier.fillMaxSize(),
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
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Your wishlist is empty",
                        style = Theme.typography.body.large.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Theme.colors.secondaryFont
                    )
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
