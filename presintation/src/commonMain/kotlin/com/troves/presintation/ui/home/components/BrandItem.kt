package com.troves.presintation.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.bounceClick
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BrandItem(
    name: String,
    imagePainter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.bounceClick(
            shape = RoundedCornerShape(10.dp),
            onClick = onClick
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Theme.colors.surfaceVariant)
                .border(1.dp, Theme.colors.onPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = imagePainter,
                contentDescription = name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        BasicText(
            text = name,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Normal, // خط ناعم وانسيابي مثل الصورة تماماً
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview
@Composable
private fun BrandItemPreview() {
    SpTheme {
        Box(modifier = Modifier.background(Theme.colors.backGround).padding(16.dp)) {
            BrandItem(
                name = "Nike",
                imagePainter = ColorPainter(Color.Gray),
                onClick = {}
            )
        }
    }
}