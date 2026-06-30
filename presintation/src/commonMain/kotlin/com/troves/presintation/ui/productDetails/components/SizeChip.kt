package com.troves.presintation.ui.productDetails.components
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme

@Composable
fun SizeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strokeColor = if (isSelected) Theme.colors.onDisable else Theme.colors.disable
    val strokeWidth = if (isSelected) 1.5.dp else 1.dp
    val labelColor  = if (isSelected) Theme.colors.warning else Theme.colors.onPrimary
    val bgColor     = if (isSelected) Theme.colors.primary.copy(alpha = 0.05f) else Theme.colors.amber

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(Theme.shapes.large)
            .background(bgColor)
            .border(strokeWidth, strokeColor, Theme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            style = Theme.typography.body.medium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = labelColor,
        )
    }
}
