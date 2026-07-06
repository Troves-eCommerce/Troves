package com.troves.designsystem.components.chip

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.bounceClick


@Composable
fun AppChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    enabled: Boolean = true,
    containerColor: Color? = null,
    contentColor: Color? = null,
    borderColor: Color? = null,
) {
    val targetContainer = containerColor ?: when {
        !enabled -> Theme.colors.disable
        selected -> Theme.colors.primary
        else -> Theme.colors.surface
    }
    val targetContent = contentColor ?: when {
        !enabled -> Theme.colors.onDisable
        selected -> Theme.colors.onPrimary
        else -> Theme.colors.primaryFont
    }
    val targetBorder = borderColor ?: when {
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
            .then(
                if (enabled) {
                    Modifier.bounceClick(
                        shape = RoundedCornerShape(10.dp),
                        onClick = onClick
                    )
                } else {
                    Modifier.clip(shape)
                }
            )
            .background(containerColor)
            .border(BorderStroke(1.dp, borderColor), shape)
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
