package com.example.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.LayoutDirection
import com.example.designsystem.theme.color.ColorScheme
import com.example.designsystem.theme.color.lightColors
import com.example.designsystem.theme.color.localSPColorScheme
import com.example.designsystem.dimensions.LocalSPShapes
import com.example.designsystem.dimensions.LocalSPSpacing
import com.example.designsystem.dimensions.SPShapes
import com.example.designsystem.dimensions.SPSpacing
import com.example.designsystem.theme.typo.LocalSPFontFamily
import com.example.designsystem.theme.typo.LocalSPTypography
import com.example.designsystem.theme.typo.SPTextStyle
import com.example.designsystem.theme.typo.arabicFontFamily
import com.example.designsystem.theme.typo.defaultSPTypographyForLanguage
import com.example.designsystem.theme.typo.spTypographyOf

@Composable
fun SpTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    locale: Locale = Locale.current,
    colors: ColorScheme = if (isDarkTheme) lightColors else lightColors,
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
        content = content,
    )
}

private fun isRtlLocale(locale: Locale): Boolean =
    when (locale.language) {
        "ar", "fa", "he", "iw", "ur" -> true
        else -> false
    }