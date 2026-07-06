package com.troves.designsystem.components.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.troves.designsystem.theme.Theme

@Composable
fun OrderStatusBadge(
    status: String,
    modifier: Modifier = Modifier,
) {
    val statusLower = status.lowercase()
    val (backgroundColor, textColor) = when {
        statusLower.contains("process") -> Theme.colors.primary.copy(alpha = 0.1f) to Theme.colors.primary
        statusLower.contains("ship") -> Theme.colors.amber.copy(alpha = 0.1f) to Theme.colors.amber
        statusLower.contains("delivery") -> Theme.colors.warning.copy(alpha = 0.1f) to Theme.colors.warning
        statusLower.contains("deliver") -> Theme.colors.success.copy(alpha = 0.1f) to Theme.colors.success
        statusLower.contains("cancel") -> Theme.colors.error.copy(alpha = 0.1f) to Theme.colors.error
        else -> Theme.colors.hint.copy(alpha = 0.1f) to Theme.colors.hint
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(backgroundColor)
            .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.extraSmall),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = status,
            style = Theme.typography.body.small.copy(
                color = textColor,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
