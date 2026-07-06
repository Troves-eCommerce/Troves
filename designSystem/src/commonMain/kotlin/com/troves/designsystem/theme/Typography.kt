package com.troves.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.*

internal val defaultFontFamily: FontFamily
    @Composable
    get() = FontFamily(
        Font(
            resource = Res.font.cairo_extralight,
            weight = FontWeight.ExtraLight
        ),
        Font(
            resource = Res.font.cairo_light,
            weight = FontWeight.Light
        ),
        Font(
            resource = Res.font.cairo_regular,
            weight = FontWeight.Normal
        ),
        Font(
            resource = Res.font.cairo_medium,
            weight = FontWeight.Medium
        ),
        Font(
            resource = Res.font.cairo_semibold,
            weight = FontWeight.SemiBold
        ),
        Font(
            resource = Res.font.cairo_bold,
            weight = FontWeight.Bold
        ),
        Font(
            resource = Res.font.cairo_extrabold,
            weight = FontWeight.ExtraBold
        ),
        Font(
            resource = Res.font.cairo_black,
            weight = FontWeight.Black
        )
    )

data class SPTextStyle(
    val display: TextStyle,
    val title: TextStyle,
    val body: SizedTextStyle,
    val hint: SizedTextStyle,
    val displayMedium: TextStyle
)

internal val LocalSPTypography = staticCompositionLocalOf<SPTextStyle> {
    error("No typography provided")
}

internal val LocalSPFontFamily = staticCompositionLocalOf<FontFamily> {
    error("No font family provided")
}

data class SizedTextStyle(
    val large: TextStyle,
    val medium: TextStyle,
    val small: TextStyle
)

@Composable
internal fun spTypographyOf(fontFamily: FontFamily): SPTextStyle = SPTextStyle(
    display = TextStyle(
        fontFamily = fontFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 42.sp
    ),
    displayMedium = TextStyle(
        fontFamily = fontFamily,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 34.sp
    ),
    title = TextStyle(
        fontFamily = fontFamily,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp
    ),
    body = SizedTextStyle(
        large = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp
        ),
        medium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp
        ),
        small = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.sp
        )
    ),
    hint = SizedTextStyle(
        large = TextStyle(
            fontFamily = fontFamily,
            fontSize = 17.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 26.sp
        ),
        medium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 20.sp
        ),
        small = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 16.sp
        )
    )
)

@Composable
internal fun defaultSPTypographyForLanguage(languageCode: String): SPTextStyle =
    spTypographyOf(fontFamily = defaultFontFamily)