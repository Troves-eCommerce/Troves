package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OptionSelectorRow(
    values: List<String>,
    selectedValue: String?,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        values.forEach { value ->
            FilterChip(
                selected = value == selectedValue,
                onClick = { onValueSelected(value) },
                colors = FilterChipDefaults.filterChipColors(),
                label = {
                    Text(
                        text = value,
                        style = Theme.typography.body.medium,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
            )
        }
    }
}
