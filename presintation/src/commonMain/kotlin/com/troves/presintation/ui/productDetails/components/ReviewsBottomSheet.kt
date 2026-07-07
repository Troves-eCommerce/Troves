package com.troves.presintation.ui.productDetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.troves.designsystem.components.bottomsheet.BaseBottomSheet
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.textfield.CustomTextField
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.noRippleClickable
import com.troves.presintation.ui.productDetails.models.ReviewDraft
import com.troves.presintation.ui.productDetails.models.ReviewUi
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_star
import troves.designsystem.generated.resources.product_details_review_comment_hint
import troves.designsystem.generated.resources.product_details_review_editor_title
import troves.designsystem.generated.resources.product_details_review_first_name_hint
import troves.designsystem.generated.resources.product_details_review_last_name_hint
import troves.designsystem.generated.resources.product_details_review_rating_label
import troves.designsystem.generated.resources.product_details_review_submit
import troves.designsystem.generated.resources.product_details_reviews_edit_yours
import troves.designsystem.generated.resources.product_details_reviews_empty_subtitle
import troves.designsystem.generated.resources.product_details_reviews_empty_title
import troves.designsystem.generated.resources.product_details_reviews_title
import troves.designsystem.generated.resources.product_details_reviews_write

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsBottomSheet(
    reviews: List<ReviewUi>,
    myReview: ReviewUi?,
    showEditor: Boolean,
    draft: ReviewDraft,
    isSubmitting: Boolean,
    onDraftChange: (ReviewDraft) -> Unit,
    onWriteReview: () -> Unit,
    onDismissEditor: () -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BaseBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        // Header: back arrow (editor only) + contextual title + close.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showEditor) {
                    Icon(
                        painter = rememberVectorPainter(Lucide.ChevronLeft),
                        contentDescription = null,
                        tint = Theme.colors.primaryFont,
                        modifier = Modifier
                            .clip(CircleShape)
                            .noRippleClickable(onClick = onDismissEditor)
                            .size(24.dp),
                    )
                    Spacer(Modifier.size(8.dp))
                }
                Text(
                    text = if (showEditor) {
                        stringResource(Res.string.product_details_review_editor_title)
                    } else {
                        stringResource(Res.string.product_details_reviews_title)
                    },
                    style = Theme.typography.title,
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont,
                )
            }
            Icon(
                painter = rememberVectorPainter(Lucide.X),
                contentDescription = null,
                tint = Theme.colors.secondaryFont,
                modifier = Modifier
                    .clip(CircleShape)
                    .noRippleClickable(onClick = onDismiss)
                    .size(22.dp),
            )
        }

        AnimatedContent(targetState = showEditor, label = "reviewsSheetMode") { editing ->
            if (editing) {
                ReviewEditor(
                    draft = draft,
                    isSubmitting = isSubmitting,
                    onDraftChange = onDraftChange,
                    onSubmit = onSubmit,
                )
            } else {
                ReviewsList(
                    reviews = reviews,
                    myReview = myReview,
                    onWriteReview = onWriteReview,
                )
            }
        }
    }
}

@Composable
private fun ReviewsList(
    reviews: List<ReviewUi>,
    myReview: ReviewUi?,
    onWriteReview: () -> Unit,
) {
    // Pin the user's own review to the top; it's also highlighted by CustomerReviewItem.
    val ordered = (listOfNotNull(myReview) + reviews.filterNot { it.isMine })

    Column(modifier = Modifier.fillMaxWidth()) {
        if (ordered.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.spacing.medium, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
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
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .padding(horizontal = Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(ordered, key = { it.id }) { review ->
                    CustomerReviewItem(review = review)
                }
            }
        }

        Box(modifier = Modifier.padding(Theme.spacing.medium)) {
            PrimaryButton(
                caption = if (myReview != null) {
                    stringResource(Res.string.product_details_reviews_edit_yours)
                } else {
                    stringResource(Res.string.product_details_reviews_write)
                },
                onClick = onWriteReview,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ReviewEditor(
    draft: ReviewDraft,
    isSubmitting: Boolean,
    onDraftChange: (ReviewDraft) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.product_details_review_rating_label),
            style = Theme.typography.body.medium,
            fontWeight = FontWeight.Bold,
            color = Theme.colors.primaryFont,
        )
        RatingSelector(
            rating = draft.rating,
            onRatingChange = { onDraftChange(draft.copy(rating = it)) },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CustomTextField(
                text = draft.firstName,
                onTextChange = { onDraftChange(draft.copy(firstName = it)) },
                hint = stringResource(Res.string.product_details_review_first_name_hint),
                singleLine = true,
                enabled = !isSubmitting,
                containerColor = Theme.colors.surface,
                borderColor = Theme.colors.disable,
                modifier = Modifier.weight(1f),
            )
            CustomTextField(
                text = draft.lastName,
                onTextChange = { onDraftChange(draft.copy(lastName = it)) },
                hint = stringResource(Res.string.product_details_review_last_name_hint),
                singleLine = true,
                enabled = !isSubmitting,
                containerColor = Theme.colors.surface,
                borderColor = Theme.colors.disable,
                modifier = Modifier.weight(1f),
            )
        }

        CustomTextField(
            text = draft.comment,
            onTextChange = { onDraftChange(draft.copy(comment = it)) },
            hint = stringResource(Res.string.product_details_review_comment_hint),
            singleLine = false,
            minLines = 3,
            enabled = !isSubmitting,
            containerColor = Theme.colors.surface,
            borderColor = Theme.colors.disable,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(4.dp))

        PrimaryButton(
            caption = stringResource(Res.string.product_details_review_submit),
            onClick = onSubmit,
            isLoading = isSubmitting,
            isDisabled = draft.rating <= 0,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun RatingSelector(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (star in 1..5) {
            Icon(
                painter = painterResource(Res.drawable.ic_star),
                contentDescription = null,
                tint = if (star <= rating) Theme.colors.warning else Theme.colors.disable,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { onRatingChange(star) }
                    .padding(2.dp),
            )
        }
    }
}
