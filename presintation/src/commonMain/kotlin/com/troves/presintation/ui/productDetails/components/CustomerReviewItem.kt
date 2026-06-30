package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.productDetails.models.ReviewUi

@Composable
fun CustomerReviewItem(
    review: ReviewUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = review.authorName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Theme.colors.secondary,
            )
            StarRatingDisplay(rating = review.rating)
        }

        if (review.date.isNotEmpty()) {
            Text(
                text = review.date,
                style = MaterialTheme.typography.bodySmall,
                color = Theme.colors.secondary,
            )
        }

        if (review.comment.isNotEmpty()) {
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodySmall,
                color = Theme.colors.secondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
            )
        }
    }
}
