package com.troves.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.troves.designsystem.dimensions.LocalSPShapes
import com.troves.designsystem.dimensions.LocalSPSize
import com.troves.designsystem.dimensions.LocalSPSpacing
import com.troves.designsystem.dimensions.SPShapes
import com.troves.designsystem.dimensions.SPSize
import com.troves.designsystem.dimensions.SPSpacing

@Composable
fun SpTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    languageCode: String = "en",
    content: @Composable () -> Unit,
) {
    val colors = remember(isDarkTheme) {
        if (isDarkTheme) darkColors else lightColors
    }

    val typography = defaultSPTypographyForLanguage(languageCode)

    val layoutDirection = remember(languageCode) {
        if (isRtlLanguage(languageCode)) LayoutDirection.Rtl else LayoutDirection.Ltr
    }

    val fontFamily = if (languageCode == "ar") arabicFontFamily else defaultFontFamily

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        localSPColorScheme provides colors,
        LocalSPTypography provides typography,
        LocalSPFontFamily provides fontFamily,
        LocalSPSpacing provides remember { SPSpacing() },
        LocalSPShapes provides remember { SPShapes() },
        LocalSPSize provides remember { SPSize() },
        content = content,
    )
}

private fun isRtlLanguage(languageCode: String): Boolean =
    when (languageCode) {
        "ar", "fa", "he", "iw", "ur" -> true
        else -> false
    }