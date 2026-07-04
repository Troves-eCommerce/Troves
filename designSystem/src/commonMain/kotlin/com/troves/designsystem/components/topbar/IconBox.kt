package com.troves.designsystem.components.topbar

import androidx.compose.foundation.BorderStroke // Added
import androidx.compose.foundation.background
import androidx.compose.foundation.border     // Added
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme

@Composable
fun IconBox(
    icon: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = Theme.colors.surface,
    iconTint: Color = Theme.colors.primaryFont,
    shape: Shape = RoundedCornerShape(CORNER_RADIUS),
    border: BorderStroke? = null,
) {
    Box(
        modifier = modifier
            .size(BOX_SIZE)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .clip(shape)
            .background(backgroundColor)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(ICON_SIZE),
        )
    }
}

private val BOX_SIZE = 40.dp
private val CORNER_RADIUS = 12.dp
private val ICON_SIZE = 20.dp

@Preview
@Composable
private fun IconBoxPreview() {
    SpTheme {
        IconBox(
            icon = ColorPainter(Color.Black),
            onClick = {},
            border = BorderStroke(1.dp, Color.LightGray)
        )
    }
}