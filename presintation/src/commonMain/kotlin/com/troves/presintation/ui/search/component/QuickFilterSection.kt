package com.troves.presintation.ui.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Category

@Composable
fun QuickFilterSection(
    categories: List<Category>,
    selectedCategoryIds: Set<String>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) return

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(categories, key = { it.id }) { category ->
            val isSelected = selectedCategoryIds.contains(category.id.toString())
            QuickFilterChip(
                text = category.name,
                isSelected = isSelected,
                onClick = { onCategoryClick(category.id.toString()) }
            )
        }
    }
}

@Composable
private fun QuickFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Theme.colors.primary else Color.Transparent
    val contentColor = if (isSelected) Color.White else Theme.colors.primaryFont
    val borderColor = if (isSelected) Color.Transparent else Theme.colors.hint.copy(alpha = 0.5f)

    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = contentColor,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
