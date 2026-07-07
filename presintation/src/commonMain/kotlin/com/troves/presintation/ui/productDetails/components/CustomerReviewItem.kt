package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.productDetails.models.ReviewUi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_star
import troves.designsystem.generated.resources.product_details_reviews_your_badge

@Composable
fun CustomerReviewItem(
    review: ReviewUi,
    modifier: Modifier = Modifier,
    fixedHeight: Dp? = null,
) {
    val shape = RoundedCornerShape(12.dp)
    val background = if (review.isMine) Theme.colors.primary.copy(alpha = 0.08f) else Theme.colors.surface
    val borderColor = if (review.isMine) Theme.colors.primary else Theme.colors.disable

    val containerModifier = modifier
        .fillMaxWidth()
        .let { if (fixedHeight != null) it.height(fixedHeight) else it }
        .clip(shape)
        .background(background)
        .border(width = 1.dp, color = borderColor, shape = shape)
        .padding(16.dp)

    if (fixedHeight != null) {
        // Fixed-size card (horizontal preview row): comment flexes, date pinned to the bottom.
        Column(modifier = containerModifier, verticalArrangement = Arrangement.Top) {
            ReviewHeader(review)
            Spacer(modifier = Modifier.height(8.dp))
            if (review.comment.isNotEmpty()) {
                Text(
                    text = review.comment,
                    style = Theme.typography.body.small,
                    color = Theme.colors.secondaryFont,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp,
                    modifier = Modifier.weight(1f),
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            if (review.date.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                ReviewDate(review.date)
            }
        }
    } else {
        // Wrap-height card (bottom-sheet list).
        Column(modifier = containerModifier, verticalArrangement = Arrangement.Top) {
            ReviewHeader(review)
            if (review.comment.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = review.comment,
                    style = Theme.typography.body.small,
                    color = Theme.colors.secondaryFont,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp,
                )
            }
            if (review.date.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                ReviewDate(review.date)
            }
        }
    }
}

@Composable
private fun ColumnScope.ReviewHeader(review: ReviewUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = review.authorName,
                style = Theme.typography.body.medium,
                fontWeight = FontWeight.Bold,
                color = Theme.colors.primaryFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (review.isMine) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(Res.string.product_details_reviews_your_badge),
                    style = Theme.typography.body.small,
                    fontWeight = FontWeight.Medium,
                    color = Theme.colors.primary,
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(Res.drawable.ic_star),
                contentDescription = null,
                tint = Theme.colors.warning,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = review.rating.toString(),
                style = Theme.typography.body.medium,
                fontWeight = FontWeight.Bold,
                color = Theme.colors.primaryFont,
            )
        }
    }
}

@Composable
private fun ReviewDate(date: String) {
    Text(
        text = date,
        style = Theme.typography.body.small,
        color = Theme.colors.hint,
    )
}
