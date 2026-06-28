package com.troves.presintation.ui.productDetailsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme


@Composable
fun HorizontalPageIndicator(
    pages: Int,
    selectedPage: Int,
    selectedColor: Color,
    unSelectedColor: Color,
    indicatorSize: Dp = Theme.size.small,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        for (i in 0 until pages) {
            Box(
                modifier = Modifier.size(indicatorSize).background(
                    shape = RoundedCornerShape(size = indicatorSize * 2),
                    color = if (selectedPage == i) selectedColor else unSelectedColor
                )
            )
        }
    }
}