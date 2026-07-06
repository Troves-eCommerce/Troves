package com.troves.designsystem.components.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import kotlinx.coroutines.delay

/**
 * Lightweight, cross-platform "toast" overlay (Android + iOS).
 *
 * Renders a transient message anchored to the bottom of the available space and
 * auto-dismisses after [durationMillis]. Pass a non-null [message] to show it and
 * `null` to hide it. Place it as the last child of a full-size container so it
 * overlays the screen content.
 */
@Composable
fun TrovesToast(
    message: String?,
    modifier: Modifier = Modifier,
    durationMillis: Long = 2000L,
    onDismiss: () -> Unit = {},
) {
    LaunchedEffect(message) {
        if (message != null) {
            delay(durationMillis)
            onDismiss()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = message != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .background(
                        color = Theme.colors.primaryFont.copy(alpha = 0.92f),
                        shape = Theme.shapes.medium,
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                BasicText(
                    text = message ?: "",
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.surface,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}
