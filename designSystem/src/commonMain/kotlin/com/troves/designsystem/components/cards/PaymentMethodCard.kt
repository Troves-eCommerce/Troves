package com.troves.designsystem.components.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme


@Composable
fun PaymentMethodCard(
    title: String,
    description: String,
    icon: Painter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    subDescription: String? = null,
    enabled: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    val targetContainer = when {
        !enabled -> Theme.colors.disable
        selected -> Theme.colors.surface
        else -> Theme.colors.backGround
    }
    val targetBorder = when {
        !enabled -> Theme.colors.disable
        selected -> Theme.colors.primaryVariant
        else -> Theme.colors.secondary
    }
    val containerColor by animateColorAsState(targetContainer)
    val borderColor by animateColorAsState(targetBorder)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .background(containerColor)
            .border(1.dp, borderColor, Theme.shapes.medium)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        verticalAlignment = Alignment.Top,
    ) {
        RadioIndicator(selected = selected)

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(Theme.shapes.medium)
                .background(Theme.colors.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Theme.colors.primaryFont,
                modifier = Modifier.size(Theme.size.iconMedium),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            ) {
                BasicText(
                    text = title,
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier.weight(1f),
                )
                if (label != null) {
                    LabelChip(label = label)
                }
            }

            BasicText(
                text = description,
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            )

            if (subDescription != null) {
                BasicText(
                    text = subDescription,
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.success,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            if (trailingContent != null) {
                Box(modifier = Modifier.padding(top = Theme.spacing.extraSmall)) {
                    trailingContent()
                }
            }
        }
    }
}

@Composable
private fun RadioIndicator(selected: Boolean) {
    if (selected) {
        // Donut: solid primary disc with a light center dot.
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Theme.colors.primary),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.backGround),
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(2.dp, Theme.colors.secondary, CircleShape),
        )
    }
}

@Composable
private fun LabelChip(label: String) {
    Box(
        modifier = Modifier
            .clip(Theme.shapes.small)
            .background(Theme.colors.surfaceVariant)
            .padding(horizontal = Theme.spacing.small, vertical = Theme.spacing.extraSmall),
    ) {
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Preview
@Composable
private fun PaymentMethodCardPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.backGround)
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        ) {
            PaymentMethodCard(
                title = "Cash on Delivery (COD)",
                description = "Pay with cash when your order is delivered.",
                icon = androidx.compose.ui.graphics.painter.ColorPainter(Theme.colors.primaryFont),
                selected = true,
                onClick = {},
                label = "COD",
                subDescription = "Cash limit: up to \$500.00",
            )
            PaymentMethodCard(
                title = "Online Payment",
                description = "Pay securely using your card.",
                icon = androidx.compose.ui.graphics.painter.ColorPainter(Theme.colors.primaryFont),
                selected = false,
                onClick = {},
                trailingContent = {
                    Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                        listOf("VISA", "MC", "AMEX").forEach {
                            BasicText(
                                text = it,
                                style = Theme.typography.body.small.copy(
                                    color = Theme.colors.secondaryFont,
                                    fontWeight = FontWeight.Bold,
                                ),
                            )
                        }
                    }
                },
            )
        }
    }
}
