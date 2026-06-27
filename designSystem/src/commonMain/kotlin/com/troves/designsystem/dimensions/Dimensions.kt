package com.troves.designsystem.dimensions

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SPSpacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp
)

data class SPShapes(
    val small: Shape = RoundedCornerShape(4.dp),
    val medium: Shape = RoundedCornerShape(8.dp),
    val large: Shape = RoundedCornerShape(16.dp)
)

data class SPSize(
    val small: Dp = 24.dp,
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val componentsNormalHeight: Dp = 48.dp,
    val medium: Dp = 48.dp,
    val large: Dp = 64.dp
)

val LocalSPSpacing = staticCompositionLocalOf { SPSpacing() }
val LocalSPShapes = staticCompositionLocalOf { SPShapes() }
val LocalSPSize = staticCompositionLocalOf { SPSize() }
