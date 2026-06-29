package com.troves.presintation.ui.productDetails.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
        colors.forEachIndexed { index, colorName ->
            AssistChip(
                onClick = {onColorSelected(index)},
                label = {
                    Text(
                        text = colorName,
                        style = Theme.typography.body.medium,
                        fontWeight =  FontWeight.SemiBold,
                        color = Theme.colors.primary,
                    )
                }
            )
        }
    }
}