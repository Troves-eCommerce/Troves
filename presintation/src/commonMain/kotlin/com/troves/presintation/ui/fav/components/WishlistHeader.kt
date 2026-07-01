package com.troves.presintation.ui.fav.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme

@Composable
fun WishlistHeader(
    itemCount: Int,
    showClearButton: Boolean,
    onClearAllClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = "Your Wishlist",
            style = Theme.typography.title,
            color = Theme.colors.primaryFont,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "$itemCount items saved for later",
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont,
            )

            if (showClearButton) {

                Text(
                    text = "Clear All",
                    style = Theme.typography.body.medium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Theme.colors.primary,
                    modifier = Modifier.clickable {
                        onClearAllClick()
                    }
                )
            }
        }
    }
}