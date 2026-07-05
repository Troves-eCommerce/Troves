package com.troves.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ColorScheme(
    val primary: Color,
    val primaryVariant: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val backGround: Color,
    val primaryFont: Color,
    val secondaryFont: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val hint: Color,
    val warning: Color,
    val onWarning: Color,
    val error: Color,
    val onError: Color,
    val success: Color,
    val onSuccess: Color,
    val amber: Color,
    val onAmber: Color,
    val disable: Color,
    val onDisable: Color,
    val tint: Color
)
val lightColors = ColorScheme(
    primary = Color(0xFFA9B6A1),         // Green primary color
    primaryVariant = Color(0xFF8E9B86),  // A slightly darker variant of the green for light mode
    onPrimary = Color(0xFFECECEC),       // Dark text for better readability on green
    secondary = Color(0xFFD2D9CC),       // Light neutral secondary color
    onSecondary = Color(0xFF000000),     // Dark text for secondary surfaces
    backGround = Color(0xFFFEFDFC),
    primaryFont = Color(0xFF000000),
    secondaryFont = Color(0xFF636369),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFFDFAF6),
    onSurface = Color(0xFF000000),
    hint = Color(0xFFA7A7A7),
    warning = Color(0xFFFFC107),
    onWarning = Color(0xFF000000),
    error = Color(0xFFF44336),
    onError = Color(0xFFFFFFFF),
    success = Color(0xFF4CAF50),
    onSuccess = Color(0xFFFFFFFF),
    amber = Color(0xFFD97706),
    onAmber = Color(0xFFFFFFFF),
    disable = Color(0xF1FAFAFA),
    onDisable = Color(0xFFBDBDBD),
    tint = Color(0xFF3E4A3C),
)

val darkColors = ColorScheme(
    primary = Color(0xFFA9B6A1),         // Kept same green for consistency (or you can tint it darker)
    primaryVariant = Color(0xFF6A7563),  // Deeper green accent variant for dark backgrounds
    onPrimary = Color(0xFF121212),       // Dark contrast text for primary elements
    secondary = Color(0xFF8B9286),       // Muted, darker version of the secondary color for dark mode
    onSecondary = Color(0xFF121212),
    backGround = Color(0xFF121212),
    primaryFont = Color(0xFFEEEEEE),
    secondaryFont = Color(0xFFAAAAAA),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF1C1A22),
    onSurface = Color(0xFFEEEEEE),
    hint = Color(0xFF757575),
    warning = Color(0xFFFFD54F),
    onWarning = Color(0xFF121212),
    error = Color(0xFFCF6679),
    onError = Color(0xFF121212),
    success = Color(0xFF81C784),
    onSuccess = Color(0xFF121212),
    amber = Color(0xFFD97706),
    onAmber = Color(0xFFFFFFFF),
    disable = Color(0xFF333333),
    onDisable = Color(0xFF555555),
    tint = Color(0xFF97A594),
)

internal val localSPColorScheme = staticCompositionLocalOf { darkColors }