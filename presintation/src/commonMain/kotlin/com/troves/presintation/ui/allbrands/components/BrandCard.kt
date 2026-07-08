package com.troves.presintation.ui.allbrands.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.bounceClick

@Composable
fun BrandCard(
    name: String,
    imagePainter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .bounceClick(
                shape = RoundedCornerShape(10.dp),
                onClick = onClick
            ),
    ) {
        Image(
            painter = imagePainter,
            contentDescription = name,
            contentScale = ContentScale.FillHeight,
            colorFilter = ColorFilter.tint(
                Theme.colors.primaryFont,
                blendMode = BlendMode.SrcAtop,
            ),
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                    ),
                ),
        )

        BasicText(
            text = name,
            style = Theme.typography.body.large.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
        )
    }
}

@Preview
@Composable
private fun BrandCardPreview() {
    SpTheme {
        BrandCard(
            name = "Nike",
            imagePainter = ColorPainter(Color.DarkGray),
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(140.dp),
        )
    }
}