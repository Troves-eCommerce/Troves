package com.troves.presintation.ui.productDetails.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme


@Composable
fun ColorSelectorRow(
    colors: List<String>,
    selectedColorIndex: Int,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        colors.forEachIndexed { index, colorUi ->
            ColorCircle(
                color = Theme.colors.onDisable,
                isSelected = index == selectedColorIndex,
                contentDescription = colorUi,
                onClick = { onColorSelected(index) },
            )
        }
    }
}