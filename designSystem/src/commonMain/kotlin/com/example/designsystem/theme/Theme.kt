package com.example.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.example.designsystem.theme.color.ColorScheme
import com.example.designsystem.theme.color.localSPColorScheme
import com.example.designsystem.dimensions.LocalSPShapes
import com.example.designsystem.dimensions.LocalSPSize
import com.example.designsystem.dimensions.LocalSPSpacing
import com.example.designsystem.dimensions.SPShapes
import com.example.designsystem.dimensions.SPSize
import com.example.designsystem.dimensions.SPSpacing
import com.example.designsystem.theme.typo.LocalSPTypography
import com.example.designsystem.theme.typo.SPTextStyle

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
