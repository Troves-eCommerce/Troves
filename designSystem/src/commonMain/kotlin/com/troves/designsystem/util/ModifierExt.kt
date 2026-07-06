package com.troves.designsystem.util

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

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

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    this.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick,
    )
}

fun Modifier.bounceClick(
    shape: Shape,
    maxPadding: Dp = 6.dp,
    onClick: () -> Unit,
): Modifier = this.then(
    Modifier.composed {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val animatedPadding by animateDpAsState(
            targetValue = if (isPressed) maxPadding else 0.dp,
            label = "bounceClickPadding"
        )

        Modifier
            .padding(animatedPadding)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
    }
)
