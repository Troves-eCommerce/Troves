package com.troves.designsystem.components.chip

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme


@Composable
fun AppChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    enabled: Boolean = true,
) {
    val targetContainer = when {
        !enabled -> Theme.colors.disable
        selected -> Theme.colors.primary
        else -> Theme.colors.surface
    }
    val targetContent = when {
        !enabled -> Theme.colors.onDisable
        selected -> Theme.colors.onPrimary
        else -> Theme.colors.primaryFont
    }
    val targetBorder = when {
        !enabled -> Theme.colors.disable
        selected -> Theme.colors.primary
        else -> Theme.colors.hint
    }

    val containerColor by animateColorAsState(targetContainer)
    val contentColor by animateColorAsState(targetContent)
    val borderColor by animateColorAsState(targetBorder)

    val shape = RoundedCornerShape(percent = 50)

    Row(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .border(BorderStroke(1.dp, borderColor), shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
    ) {
        if (leadingIcon != null) {
            Icon(
                painter = leadingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(Theme.size.iconSmall),
            )
        }
        BasicText(
            text = label,
            style = Theme.typography.body.medium.copy(
                color = contentColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            ),
        )
    }
}
