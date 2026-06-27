package com.troves.designsystem.components.button

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter

import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme

@Composable
fun IconButton(
    onClick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier,
    containerColor: Color = Theme.colors.primary,
    iconColor: Color = Theme.colors.onPrimary,
    isDisabled: Boolean = false,
) {
    BaseButton(
        caption = null,
        modifier = modifier.size(58.dp),
        iconPainter = icon,
        containerColor = containerColor,
        contentColor = iconColor,
        onClick = onClick,
        isDisabled = isDisabled,
    )
}


@Composable
private fun IconButtonPreview() {
    SpTheme() {
        IconButton(
            onClick = {},
            icon = ColorPainter(Color.Black)
        )
    }
}
