package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.productDetails.models.ReviewUi
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.product_details_reviews_empty_subtitle
import troves.designsystem.generated.resources.product_details_reviews_empty_title
import troves.designsystem.generated.resources.product_details_reviews_see_all
import troves.designsystem.generated.resources.product_details_reviews_title
import troves.designsystem.generated.resources.product_details_reviews_write

private const val PREVIEW_COUNT = 3

private val CARD_WIDTH = 280.dp
private val CARD_HEIGHT = 160.dp

@Composable
fun CustomerReviewsSection(
    reviews: List<ReviewUi>,
    isLoading: Boolean,
    onSeeAllClick: () -> Unit,
    onWriteReviewClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionHeaderRow(
            title = stringResource(Res.string.product_details_reviews_title),
            actionLabel = if (reviews.isNotEmpty()) stringResource(Res.string.product_details_reviews_see_all) else null,
            onActionClick = onSeeAllClick.takeIf { reviews.isNotEmpty() },
        )

        if (reviews.isEmpty()) {
            if (!isLoading) ReviewsEmptyState(onWriteReviewClick = onWriteReviewClick)
        } else {
            val preview = reviews.sortedByDescending { it.isMine }.take(PREVIEW_COUNT)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 2.dp),
            ) {
                items(preview, key = { it.id }) { review ->
                    CustomerReviewItem(
                        review = review,
                        modifier = Modifier.width(CARD_WIDTH),
                        fixedHeight = CARD_HEIGHT,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewsEmptyState(
    onWriteReviewClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.disable, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.product_details_reviews_empty_title),
            style = Theme.typography.body.medium,
            fontWeight = FontWeight.Bold,
            color = Theme.colors.primaryFont,
        )
        Text(
            text = stringResource(Res.string.product_details_reviews_empty_subtitle),
            style = Theme.typography.body.small,
            color = Theme.colors.secondaryFont,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.product_details_reviews_write),
            style = Theme.typography.body.small,
            fontWeight = FontWeight.Bold,
            color = Theme.colors.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onWriteReviewClick)
                .padding(horizontal = 8.dp, vertical = 6.dp),
        )
    }
}
