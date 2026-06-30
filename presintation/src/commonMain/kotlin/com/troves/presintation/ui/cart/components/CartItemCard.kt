package com.troves.presintation.ui.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.cart.CartItemUi

@Composable
fun CartItemCard(
    item: CartItemUi,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(Theme.shapes.large)
                .background(Theme.colors.surfaceVariant),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            BasicText(
                text = item.title,
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.SemiBold,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            BasicText(
                text = item.description,
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    text = "$${item.price}",
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                QuantityStepper(
                    quantity = item.quantity,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement,
                )
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        StepperButton(label = "\u2212", onClick = onDecrement)

        BasicText(
            text = quantity.toString().padStart(2, '0'),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Medium,
            ),
        )

        StepperButton(label = "+", onClick = onIncrement)
    }
}

@Composable
private fun StepperButton(
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Theme.colors.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = label,
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}
