package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.troves.presintation.ui.productDetails.models.ReviewUi

@Composable
fun CustomerReviewItem(
    review: ReviewUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF9F9F9))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = review.authorName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
            )
            StarRatingRow(rating = review.rating.toFloat(), reviewCount = 0)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (review.comment.isNotEmpty()) {
            Text(
                text = review.comment,
                fontSize = 13.sp,
                color = Color(0xFF555555),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 19.sp,
            )
        }
    }
}