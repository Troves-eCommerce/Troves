package com.troves.designsystem.util

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ripple
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

import com.troves.designsystem.theme.Theme

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

fun Modifier.bounceClick(
    shape: Shape,
    maxPadding: Dp = 6.dp,
    rippleColor: Color? = null,
    onClick: () -> Unit
): Modifier = this.then(
    Modifier.composed {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        
        val finalRippleColor = rippleColor ?: Theme.colors.primaryFont.copy(alpha = 0.2f)
        
        val animatedPadding by animateDpAsState(
            targetValue = if (isPressed) maxPadding else 0.dp,
            label = "bounceClickPadding"
        )

        Modifier
            .padding(animatedPadding)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = finalRippleColor),
                onClick = onClick
            )
    }
)
