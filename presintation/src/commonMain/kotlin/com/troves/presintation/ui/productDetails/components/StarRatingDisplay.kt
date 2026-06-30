package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_star

@Composable
fun StarRatingDisplay(
    rating: Int,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
) {
    Row(modifier = modifier) {
        repeat(maxStars) { index ->
            Icon(
                painter = painterResource(Res.drawable.ic_star),
                contentDescription = null,
                tint = if (index < rating) Theme.colors.warning else Theme.colors.onDisable,
                modifier = Modifier.size(Theme.size.iconSmall),
            )
        }
    }
}
