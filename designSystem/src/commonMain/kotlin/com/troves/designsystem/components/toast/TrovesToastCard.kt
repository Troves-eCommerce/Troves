package com.troves.designsystem.components.toast

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme

enum class ToastType { Success, Error, Warning, Info }

enum class ToastPosition { Top, Bottom }

@Composable
fun TrovesToastCard(
    message: String,
    type: ToastType,
    modifier: Modifier = Modifier,
) {
    val accent = accentFor(type)
    Row(
        modifier = modifier
            .widthIn(max = 520.dp)
            .shadow(10.dp, Theme.shapes.medium)
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surface)
            .border(1.dp, accent.copy(alpha = 0.55f), Theme.shapes.medium)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ToastGlyph(type = type, accent = accent, glyph = Theme.colors.surface)
        BasicText(
            text = message,
            style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont),
        )
    }
}

@Composable
private fun accentFor(type: ToastType): Color = when (type) {
    ToastType.Success -> Theme.colors.success
    ToastType.Error -> Theme.colors.error
    ToastType.Warning -> Theme.colors.warning
    ToastType.Info -> Theme.colors.primary
}

@Composable
private fun ToastGlyph(type: ToastType, accent: Color, glyph: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        drawCircle(color = accent)
        val w = size.width
        val h = size.height
        val stroke = w * 0.1f
        when (type) {
            ToastType.Success -> {
                val path = Path().apply {
                    moveTo(w * 0.28f, h * 0.52f)
                    lineTo(w * 0.44f, h * 0.68f)
                    lineTo(w * 0.74f, h * 0.34f)
                }
                drawPath(
                    path = path,
                    color = glyph,
                    style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }
            ToastType.Error -> {
                drawLine(glyph, Offset(w * 0.34f, h * 0.34f), Offset(w * 0.66f, h * 0.66f), stroke, StrokeCap.Round)
                drawLine(glyph, Offset(w * 0.66f, h * 0.34f), Offset(w * 0.34f, h * 0.66f), stroke, StrokeCap.Round)
            }
            ToastType.Warning -> {
                drawLine(glyph, Offset(w * 0.5f, h * 0.28f), Offset(w * 0.5f, h * 0.58f), stroke, StrokeCap.Round)
                drawCircle(glyph, radius = stroke * 0.6f, center = Offset(w * 0.5f, h * 0.72f))
            }
            ToastType.Info -> {
                drawCircle(glyph, radius = stroke * 0.6f, center = Offset(w * 0.5f, h * 0.3f))
                drawLine(glyph, Offset(w * 0.5f, h * 0.44f), Offset(w * 0.5f, h * 0.72f), stroke, StrokeCap.Round)
            }
        }
    }
}
