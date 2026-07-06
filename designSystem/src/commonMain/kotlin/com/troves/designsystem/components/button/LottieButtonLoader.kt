package com.troves.designsystem.components.button

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun LottieButtonLoader(
    tintColor: Color,
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        color = tintColor,
        modifier = modifier.size(24.dp),
        strokeWidth = 2.dp
    )
}
