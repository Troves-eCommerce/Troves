package com.troves.presintation.ui.allbrands.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme

@Composable
fun BrandCard(
    name: String,
    imagePainter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surface) // خلفية الكارد البيضاء النظيفة
            .border(
                width = 1.dp,
                color = Theme.colors.primaryFont.copy(alpha = 0.05f), // إطار خفيف جداً
                shape = Theme.shapes.medium
            )
            .clickable(onClick = onClick)
            .padding(Theme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // عرض الأيقونة المحلية الثابتة
        Image(
            painter = imagePainter,
            contentDescription = name,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Theme.colors.primaryFont), // تلوين الأيقونة بلون خط التطبيق الأساسي لتوحيد الشكل
            modifier = Modifier.size(36.dp) // حجم متناسق داخل الكارد المربع
        )

        Spacer(modifier = Modifier.height(Theme.spacing.small))

        // اسم البراند
        BasicText(
            text = name,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        )
    }
}