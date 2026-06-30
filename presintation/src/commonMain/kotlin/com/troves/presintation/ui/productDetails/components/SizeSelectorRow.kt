package com.troves.presintation.ui.productDetails.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
            AssistChip(
                onClick = {onSizeSelected(lable)},
                label = {
                    Text(
                        text = lable,
                        style = Theme.typography.body.medium,
                        fontWeight =  FontWeight.SemiBold,
                        color = Theme.colors.primary,
                    )
                }
            )
        }
    }
}
