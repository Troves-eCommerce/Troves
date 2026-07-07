package com.troves.designsystem.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.bounceClick
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_full_heart
import troves.designsystem.generated.resources.ic_solid_heart

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 32.dp,
    iconSize: androidx.compose.ui.unit.Dp = 18.dp
) {
    val backgroundColor = Color.White
    val contentColor = if (isFavorite) Color.Red else Color.Black
    val icon = if (isFavorite) Res.drawable.ic_full_heart else Res.drawable.ic_solid_heart
    val shape = Theme.shapes.large

    Box(
        modifier = modifier
            .size(size)
            .bounceClick(
                shape = RoundedCornerShape(10.dp),
                onClick = onClick
            )
            .background(backgroundColor, shape = shape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = contentColor,
            modifier = Modifier.size(iconSize)
        )
    }
}
