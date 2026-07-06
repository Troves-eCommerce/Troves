package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.troves.presintation.ui.productDetails.models.ReviewUi
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.product_details_reviews_see_all
import troves.designsystem.generated.resources.product_details_reviews_show_less
import troves.designsystem.generated.resources.product_details_reviews_title

@Composable
fun CustomerReviewsSection(
    reviews: List<ReviewUi>,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val showSeeAll = reviews.size > 2
    val reviewsToShow = if (isExpanded) reviews else reviews.take(2)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeaderRow(
            title = stringResource(Res.string.product_details_reviews_title),
            actionLabel = if (showSeeAll) {
                if (isExpanded) stringResource(Res.string.product_details_reviews_show_less) else stringResource(Res.string.product_details_reviews_see_all)
            } else null,
            onActionClick = {
                isExpanded = !isExpanded
                onSeeAllClick()
            },
        )

        reviewsToShow.forEach { review ->
            CustomerReviewItem(review = review)
        }
    }
}
