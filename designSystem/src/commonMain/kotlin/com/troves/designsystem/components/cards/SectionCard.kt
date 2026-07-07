package com.troves.designsystem.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.noRippleClickable

/**
 * A generic titled surface card: a header row (title + optional trailing action
 * link such as "Change" / "Edit Cart") above arbitrary [content]. Rounded 8dp,
 * [Theme.colors.surface] background with a subtle [Theme.colors.surfaceVariant] border.
 *
 * @param actionText optional trailing link label; shown only when [onActionClick] is also provided.
 */
@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
//            .shadow(elevation = 2.dp, shape = Theme.shapes.medium, clip = false)
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.surfaceVariant, Theme.shapes.medium)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                text = title,
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            if (actionText != null && onActionClick != null) {
                BasicText(
                    text = actionText,
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.primaryVariant,
                        fontWeight = FontWeight.Medium,
                    ),
                    modifier = Modifier.noRippleClickable(onClick = onActionClick),
                )
            }
        }
        content()
    }
}

@Preview
@Composable
private fun SectionCardPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.backGround)
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        ) {
            SectionCard(title = "Shipping Address", actionText = "Change", onActionClick = {}) {
                BasicText(
                    text = "Sophia Johnson\n123 Maple Street, Apartment 4B",
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                )
            }
            SectionCard(title = "Notes") {
                BasicText(
                    text = "A section card with no trailing action.",
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                )
            }
        }
    }
}
