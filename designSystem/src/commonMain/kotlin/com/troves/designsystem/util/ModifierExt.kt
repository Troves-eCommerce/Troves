package com.troves.designsystem.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

fun Modifier.autoMirror(): Modifier = composed {
    val layoutDirection = LocalLayoutDirection.current
    if (layoutDirection == LayoutDirection.Rtl) {
        this.graphicsLayer {
            scaleX = -1f
        }
    } else {
        this
    }
}
