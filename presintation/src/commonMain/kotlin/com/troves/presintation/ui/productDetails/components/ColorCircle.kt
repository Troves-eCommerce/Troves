package com.troves.presintation.ui.productDetails.components
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.troves.designsystem.theme.Theme

@Composable
fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Colour option",
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(Theme.size.medium)
            .clip(CircleShape)
            .clickable(onClick = onClick),
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(Theme.size.medium)
                    .border(Theme.spacing.extraSmall, Theme.colors.onDisable, CircleShape),
            )
        }
        Box(
            modifier = Modifier
                .size(Theme.size.medium)
                .clip(CircleShape)
                .background(color),
        )
    }
}
