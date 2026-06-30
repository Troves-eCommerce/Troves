package com.troves.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.troves.designsystem.dimensions.LocalSPShapes
import com.troves.designsystem.dimensions.LocalSPSize
import com.troves.designsystem.dimensions.LocalSPSpacing
import com.troves.designsystem.dimensions.SPShapes
import com.troves.designsystem.dimensions.SPSize
import com.troves.designsystem.dimensions.SPSpacing

object Theme {
    val colors: ColorScheme
        @Composable
        get() = localSPColorScheme.current

    val typography: SPTextStyle
        @Composable
        get() = LocalSPTypography.current

    val spacing: SPSpacing
        @Composable
        get() = LocalSPSpacing.current

    val shapes: SPShapes
        @Composable
        get() = LocalSPShapes.current

    val size: SPSize
        @Composable @ReadOnlyComposable get() = LocalSPSize.current
}
