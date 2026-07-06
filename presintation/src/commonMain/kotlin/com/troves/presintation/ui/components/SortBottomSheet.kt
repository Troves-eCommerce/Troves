package com.troves.presintation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.bottomsheet.BaseBottomSheet
import com.troves.designsystem.theme.Theme
import com.troves.domain.usecase.products.ProductSortOption
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.apply
import troves.designsystem.generated.resources.sort_best_seller
import troves.designsystem.generated.resources.sort_default
import troves.designsystem.generated.resources.sort_group_by_sub_category
import troves.designsystem.generated.resources.sort_price_high_to_low
import troves.designsystem.generated.resources.sort_price_low_to_high
import troves.designsystem.generated.resources.sort_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


enum class SortOption(
    val labelRes: StringResource,
    val criteria: ProductSortOption,
) {
    DEFAULT(Res.string.sort_default, ProductSortOption.DEFAULT),
    PRICE_LOW_TO_HIGH(Res.string.sort_price_low_to_high, ProductSortOption.PRICE_LOW_TO_HIGH),
    PRICE_HIGH_TO_LOW(Res.string.sort_price_high_to_low, ProductSortOption.PRICE_HIGH_TO_LOW),
    BEST_SELLER(Res.string.sort_best_seller, ProductSortOption.BEST_SELLER),
    GROUP_BY_SUB_CATEGORY(Res.string.sort_group_by_sub_category, ProductSortOption.GROUP_BY_SUB_CATEGORY),
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    selected: SortOption?,
    onSelect: (SortOption) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    options: List<SortOption> = SortOption.entries,
) {
    BaseBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = stringResource(Res.string.sort_title),
        footer = {
            PrimaryButton(
                caption = stringResource(Res.string.apply),
                onClick = onApply,
                modifier = Modifier.fillMaxWidth(),
                isDisabled = selected == null,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        ) {
            options.forEach { option ->
                SortOptionRow(
                    label = stringResource(option.labelRes),
                    selected = option == selected,
                    onClick = { onSelect(option) },
                )
            }
        }
    }
}

@Composable
private fun SortOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val container by animateColorAsState(
        if (selected) Theme.colors.surfaceVariant else Theme.colors.surface,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .background(container)
            .clickable(onClick = onClick)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        SelectionIndicator(selected = selected)
        BasicText(
            text = label,
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primaryFont,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            ),
        )
    }
}

@Composable
private fun SelectionIndicator(selected: Boolean) {
    val ringColor by animateColorAsState(
        if (selected) Theme.colors.primary else Theme.colors.hint,
    )
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(width = 2.dp, color = ringColor, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primary),
            )
        }
    }
}
