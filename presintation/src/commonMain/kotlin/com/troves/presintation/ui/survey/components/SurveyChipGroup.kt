package com.troves.presintation.ui.survey.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.troves.designsystem.components.chip.AppChip
import com.troves.designsystem.theme.Theme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SurveyChipGroup(
    options: List<String>,
    selectedOptions: Set<String>,
    onOptionToggled: (String) -> Unit,
    modifier: Modifier = Modifier,
    multiSelect: Boolean = true,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        options.forEach { option ->
            AppChip(
                label = option,
                selected = option in selectedOptions,
                onClick = { onOptionToggled(option) },
            )
        }
    }
}
