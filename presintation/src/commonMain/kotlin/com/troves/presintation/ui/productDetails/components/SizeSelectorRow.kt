package com.troves.presintation.ui.productDetails.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.troves.designsystem.components.button.IconButton
import com.troves.designsystem.components.topbar.BaseTopAppBar
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.theme.Theme.size
import com.troves.presintation.ui.productDetails.models.SizeUi
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back

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