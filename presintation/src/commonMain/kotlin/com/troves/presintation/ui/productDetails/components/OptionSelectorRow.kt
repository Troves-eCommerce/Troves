package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OptionSelectorRow(
    values: List<String>,
    selectedValue: String?,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    val colorMap = mapOf(
        "Black" to Color(0xFF111111),
        "Green" to Color(0xFFA3B19B),
        "Beige" to Color(0xFFE6D5BC)
    )

    val isColorOption = values.any { colorMap.containsKey(it) }

    if (isColorOption) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            values.forEach { value ->
                ColorCircle(
                    color = colorMap[value] ?: Color.Gray,
                    isSelected = value == selectedValue,
                    onClick = { onValueSelected(value) }
                )
            }
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            values.forEach { value ->
                SizeChip(
                    label = value,
                    isSelected = value == selectedValue,
                    onClick = { onValueSelected(value) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}