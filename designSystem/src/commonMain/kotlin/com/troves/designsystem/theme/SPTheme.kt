package com.troves.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.intl.Locale
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
    locale: Locale = Locale.current,
    colors: ColorScheme = if (isDarkTheme) darkColors else lightColors,
    fontFamily: FontFamily? = null,
    typography: SPTextStyle = fontFamily
        ?.let { spTypographyOf(it) }
        ?: defaultSPTypographyForLanguage(locale.language),
    spacing: SPSpacing = SPSpacing(),
    shapes: SPShapes = SPShapes(),
    content: @Composable () -> Unit,
) {
    val layoutDirection = if (isRtlLocale(locale)) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        localSPColorScheme provides colors,
        LocalSPTypography provides typography,
        LocalSPFontFamily provides (fontFamily ?: arabicFontFamily),
        LocalSPSpacing provides spacing,
        LocalSPShapes provides shapes,
        LocalSPSize provides SPSize(),
        content = content,
    )
}

private fun isRtlLocale(locale: Locale): Boolean =
    when (locale.language) {
        "ar", "fa", "he", "iw", "ur" -> true
        else -> false
    }