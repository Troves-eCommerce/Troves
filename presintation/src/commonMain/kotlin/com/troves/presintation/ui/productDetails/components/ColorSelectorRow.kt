package com.troves.presintation.ui.productDetails.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.troves.presintation.ui.productDetails.models.ColorUi


@Composable
fun ColorSelectorRow(
    colors: List<ColorUi>,
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
                color = colorUi.color,
                isSelected = index == selectedColorIndex,
                contentDescription = colorUi.contentDescription,
                onClick = { onColorSelected(index) },
            )
        }
    }
}