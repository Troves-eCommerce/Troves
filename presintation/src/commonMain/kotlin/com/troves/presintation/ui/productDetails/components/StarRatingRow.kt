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
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_star
import troves.designsystem.generated.resources.product_details_reviews_count

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
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = rating.toString(),
            style = Theme.typography.body.medium,
            fontWeight = FontWeight.Bold,
            color = Theme.colors.primaryFont
        )
        Text(
            text = stringResource(Res.string.product_details_reviews_count, reviewCount),
            style = Theme.typography.body.small,
            color = Theme.colors.hint,
        )
    }
}
