package com.troves.presintation.ui.aichat.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme


@Composable
fun VoiceWaveAnimation(
    modifier: Modifier = Modifier,
    color: Color = Theme.colors.primary,
    barCount: Int = 5,
) {
    val transition = rememberInfiniteTransition(label = "voiceWave")

    Row(
        modifier = modifier.height(MAX_BAR_HEIGHT.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        repeat(barCount) { index ->
            // Vary duration + phase per bar so they never move in lockstep.
            val durationMs = BASE_DURATION_MS + (index % 3) * STEP_DURATION_MS
            val scale by transition.animateFloat(
                initialValue = MIN_SCALE,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMs),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * OFFSET_MS),
                ),
                label = "voiceBar$index",
            )
            Box(
                modifier = Modifier
                    .width(BAR_WIDTH.dp)
                    .height((MAX_BAR_HEIGHT * scale).dp)
                    .clip(RoundedCornerShape(50))
                    .background(color),
            )
        }
    }
}

private const val MAX_BAR_HEIGHT = 20
private const val BAR_WIDTH = 4
private const val MIN_SCALE = 0.3f
private const val BASE_DURATION_MS = 450
private const val STEP_DURATION_MS = 130
private const val OFFSET_MS = 90
