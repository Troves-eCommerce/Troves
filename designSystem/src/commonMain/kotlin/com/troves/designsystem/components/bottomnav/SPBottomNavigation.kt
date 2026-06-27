package com.troves.designsystem.components.bottomnav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme

data class BottomNavItem(
    val label: String,
    val unselectedIcon: Painter,
    val selectedIcon: Painter,
)

@Composable
fun SPBottomNavigation(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.surface)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = index == selectedIndex
            SPBottomNavigationItem(
                item = item,
                isSelected = isSelected,
                onClick = { onItemSelected(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SPBottomNavigationItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Theme.colors.backGround else Color.Transparent
    val iconTint = Theme.colors.primary
    val textColor = Theme.colors.primaryFont
    val icon = if (isSelected) item.selectedIcon else item.unselectedIcon

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Custom ripple could be added here, omitting default for clean design
                onClick = onClick
            )
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = item.label,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        BasicText(
            text = item.label,
            style = Theme.typography.body.small.copy(
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}
