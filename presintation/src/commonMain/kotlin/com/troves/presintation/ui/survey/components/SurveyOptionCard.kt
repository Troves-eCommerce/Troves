package com.troves.presintation.ui.survey.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.noRippleClickable
import com.troves.presintation.ui.survey.StyleIcon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_explore
import troves.designsystem.generated.resources.ic_category_footwear
import troves.designsystem.generated.resources.ic_category_man
import troves.designsystem.generated.resources.ic_category_sales
import troves.designsystem.generated.resources.ic_category_women
import troves.designsystem.generated.resources.ic_star

private fun StyleIcon.toDrawableResource(): DrawableResource = when (this) {
    StyleIcon.Casual -> Res.drawable.ic_category_man
    StyleIcon.Sport -> Res.drawable.ic_category_footwear
    StyleIcon.Streetwear -> Res.drawable.ic_explore
    StyleIcon.Elegant -> Res.drawable.ic_star
    StyleIcon.Minimal -> Res.drawable.ic_category_women
    StyleIcon.Formal -> Res.drawable.ic_category_sales
}

@Composable
fun SurveyOptionCard(
    label: String,
    description: String,
    icon: StyleIcon,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = Theme.shapes.large

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colors.primary else Theme.colors.hint.copy(alpha = 0.3f),
        animationSpec = tween(150),
        label = "optionCardBorder",
    )
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 0.dp,
        animationSpec = tween(150),
        label = "optionCardElevation",
    )
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colors.primary.copy(alpha = 0.06f) else Theme.colors.surface,
        animationSpec = tween(150),
        label = "optionCardBg",
    )

    Row(
        modifier = modifier
            .semantics {
                selected = isSelected
                contentDescription = label
            }
            .shadow(elevation, shape)
            .clip(shape)
            .background(containerColor)
            .border(1.dp, borderColor, shape)
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(Theme.shapes.medium)
                .background(
                    if (isSelected) Theme.colors.primary.copy(alpha = 0.15f)
                    else Theme.colors.surfaceVariant
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon.toDrawableResource()),
                contentDescription = null,
                tint = if (isSelected) Theme.colors.primary else Theme.colors.secondaryFont,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            BasicText(
                text = label,
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                ),
            )
            BasicText(
                text = description,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                ),
            )
        }
    }
}
