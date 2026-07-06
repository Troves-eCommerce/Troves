package com.troves.designsystem.components.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.troves.designsystem.util.noRippleClickable

/**
 * A selectable saved-address card: leading radio + icon + a title row
 * ("Home" + optional "Default" label + optional "Edit" link) above the recipient
 * name, address lines, and phone.
 *
 * Selected/unselected colors mirror
 * [com.troves.designsystem.components.cards.PaymentMethodCard]: selected uses
 * [Theme.colors.surface] background, [Theme.colors.primaryVariant] border and a
 * primary "donut" radio; unselected uses [Theme.colors.backGround] background,
 * [Theme.colors.secondary] border and radio ring.
 *
 * @param label optional trailing pill next to the title (e.g. "Default").
 * @param onEditClick when non-null, an "Edit" link is shown at the trailing edge of the title row.
 */
@Composable
fun AddressCard(
    title: String,
    recipientName: String,
    addressLines: List<String>,
    phone: String,
    icon: Painter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    onEditClick: (() -> Unit)? = null,
    enabled: Boolean = true,
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
            .noRippleClickable(onClick = { if (enabled) onClick() })
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
                )
                if (label != null) {
                    LabelChip(label = label)
                }
                Spacer(modifier = Modifier.weight(1f))
                if (onEditClick != null) {
                    BasicText(
                        text = "Edit",
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.primaryVariant,
                            fontWeight = FontWeight.Medium,
                        ),
                        modifier = Modifier.noRippleClickable(onClick = { if (enabled) onEditClick() }),
                    )
                }
            }

            BasicText(
                text = recipientName,
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Medium,
                ),
            )
            addressLines.forEach { line ->
                BasicText(
                    text = line,
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                )
            }
            BasicText(
                text = phone,
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            )
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
private fun AddressCardPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.backGround)
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        ) {
            AddressCard(
                title = "Home",
                recipientName = "Sophia Johnson",
                addressLines = listOf("123 Maple Street, Apartment 4B", "San Francisco, CA 94107", "United States"),
                phone = "+1 415 555 0123",
                icon = androidx.compose.ui.graphics.painter.ColorPainter(Theme.colors.primaryFont),
                selected = true,
                onClick = {},
                label = "Default",
                onEditClick = {},
            )
            AddressCard(
                title = "Work",
                recipientName = "Sophia Johnson",
                addressLines = listOf("456 Business Park, Suite 200", "San Francisco, CA 94108", "United States"),
                phone = "+1 415 555 0456",
                icon = androidx.compose.ui.graphics.painter.ColorPainter(Theme.colors.primaryFont),
                selected = false,
                onClick = {},
                onEditClick = {},
            )
        }
    }
}
