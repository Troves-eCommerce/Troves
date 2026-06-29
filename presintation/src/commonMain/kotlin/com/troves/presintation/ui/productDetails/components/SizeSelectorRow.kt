package com.troves.presintation.ui.productDetails.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.troves.designsystem.theme.Theme

@Composable
fun SizeSelectorRow(
    sizes: List<String>,
    selectedSizeLabel: String,
    onSizeSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        sizes.forEach { lable ->
            SizeChip(
                label = lable,
                isSelected = lable == selectedSizeLabel,
                onClick = { onSizeSelected(lable) },
            )
        }
    }
}