package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_star


/**
 * Inline rating display: ★ 4.8 (124 reviews).
 *
 * @param rating      Numeric rating value (e.g. 4.8).
 * @param reviewCount Total number of reviews shown in parentheses.
 */
@Composable
fun StarRatingRow(
    rating: Float,
    reviewCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_star),
            contentDescription = null,
            tint = Theme.colors.warning,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = rating.toString(),
            style = Theme.typography.body.medium,
            fontWeight = FontWeight.SemiBold,
            color = Theme.colors.onPrimary
        )
        Text(
            text = "($reviewCount reviews)",
            style = Theme.typography.body.small,
            color = Theme.colors.onSecondary,
        )
    }
}