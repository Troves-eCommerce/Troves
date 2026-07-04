package com.troves.presintation.ui.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TrendingUp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.troves.designsystem.components.chip.AppChip
import com.troves.designsystem.theme.Theme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrendingSection(
    modifier: Modifier = Modifier,
    onTrendingChipClick: (String) -> Unit
) {
    val trendingItems = listOf(
        "Nike sneakers", "Summer dress", "Leather jacket", "Yoga pants", "Adidas running"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = rememberVectorPainter(Lucide.TrendingUp),
                contentDescription = "Trending",
                tint = Theme.colors.secondaryFont,
                modifier = Modifier.size(20.dp)
            )
            BasicText(
                text = "Trending",
                style = Theme.typography.body.small.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont
                )
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            trendingItems.forEach { item ->
                AppChip(
                    label = item,
                    selected = false,
                    onClick = { onTrendingChipClick(item) },
                    leadingIcon = rememberVectorPainter(Lucide.TrendingUp),
                    containerColor = Color(0xFFF2F2F2), // matching the light grayish bg of chips in the mockup
                    borderColor = Color.Transparent
                )
            }
        }
    }
}
