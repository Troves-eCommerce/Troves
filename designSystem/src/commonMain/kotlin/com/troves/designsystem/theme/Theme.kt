package com.troves.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.troves.designsystem.theme.color.ColorScheme
import com.troves.designsystem.theme.color.localSPColorScheme
import com.troves.designsystem.dimensions.LocalSPShapes
import com.troves.designsystem.dimensions.LocalSPSize
import com.troves.designsystem.dimensions.LocalSPSpacing
import com.troves.designsystem.dimensions.SPShapes
import com.troves.designsystem.dimensions.SPSize
import com.troves.designsystem.dimensions.SPSpacing
import com.troves.designsystem.theme.typo.LocalSPTypography
import com.troves.designsystem.theme.typo.SPTextStyle

/**
 * Convenience accessor for design-system tokens. Mirrors `MaterialTheme`'s shape
 * (`Theme.colors`, `Theme.typography`, …) so it reads naturally inside composables.
 */
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
