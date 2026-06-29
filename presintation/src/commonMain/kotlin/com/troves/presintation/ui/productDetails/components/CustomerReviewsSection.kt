package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.productDetails.models.ReviewUi

@Composable
fun CustomerReviewsSection(
    reviews: List<ReviewUi>,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeaderRow(
            title = "Customer Reviews",
            actionLabel = "See all >",
            onActionClick = onSeeAllClick,
        )
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(color = Theme.colors.hint)

        reviews.forEachIndexed { index, review ->
            CustomerReviewItem(review = review)
            if (index < reviews.lastIndex) {
                HorizontalDivider(color = Theme.colors.hint)
            }
        }
    }
}
