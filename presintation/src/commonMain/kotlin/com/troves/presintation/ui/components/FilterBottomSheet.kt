package com.troves.presintation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.button.SecondaryButton
import com.troves.designsystem.components.chip.AppChip
import com.troves.designsystem.components.bottomsheet.BaseBottomSheet
import com.troves.designsystem.theme.Theme
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.apply
import troves.presintation.generated.resources.filter_brands
import troves.presintation.generated.resources.filter_categories
import troves.presintation.generated.resources.filter_sub_categories
import troves.presintation.generated.resources.filter_title
import troves.presintation.generated.resources.reset
import org.jetbrains.compose.resources.stringResource


data class FilterOption(
    val id: String,
    val label: String,
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    categories: List<FilterOption> = emptyList(),
    subCategories: List<FilterOption> = emptyList(),
    brands: List<FilterOption> = emptyList(),
    selectedCategoryIds: Set<String> = emptySet(),
    selectedSubCategoryIds: Set<String> = emptySet(),
    selectedBrandIds: Set<String> = emptySet(),
    onToggleCategory: (String) -> Unit = {},
    onToggleSubCategory: (String) -> Unit = {},
    onToggleBrand: (String) -> Unit = {},
    onApply: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    modifier: Modifier = Modifier,
) {
    BaseBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = stringResource(Res.string.filter_title),
        footer = {
            FilterActions(
                onReset = onReset,
                onApply = onApply,
            )
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 480.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.large),
        ) {
            if (categories.isNotEmpty()) {
                FilterSection(
                    title = stringResource(Res.string.filter_categories),
                    options = categories,
                    selectedIds = selectedCategoryIds,
                    onToggle = onToggleCategory,
                )
            }
            if (subCategories.isNotEmpty()) {
                FilterSection(
                    title = stringResource(Res.string.filter_sub_categories),
                    options = subCategories,
                    selectedIds = selectedSubCategoryIds,
                    onToggle = onToggleSubCategory,
                )
            }
            if (brands.isNotEmpty()) {
                FilterSection(
                    title = stringResource(Res.string.filter_brands),
                    options = brands,
                    selectedIds = selectedBrandIds,
                    onToggle = onToggleBrand,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(
    title: String,
    options: List<FilterOption>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        BasicText(
            text = title,
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.SemiBold,
            ),
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        ) {
            options.forEach { option ->
                AppChip(
                    label = option.label,
                    selected = option.id in selectedIds,
                    onClick = { onToggle(option.id) },
                )
            }
        }
    }
}

@Composable
private fun FilterActions(
    onReset: () -> Unit,
    onApply: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        SecondaryButton(
            caption = stringResource(Res.string.reset),
            onClick = onReset,
            modifier = Modifier.weight(1f),
        )
        PrimaryButton(
            caption = stringResource(Res.string.apply),
            onClick = onApply,
            modifier = Modifier.weight(1f),
        )
    }
}
