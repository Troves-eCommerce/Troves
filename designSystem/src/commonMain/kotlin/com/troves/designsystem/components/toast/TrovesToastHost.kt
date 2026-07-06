package com.troves.designsystem.components.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

data class ToastData(
    val message: String,
    val type: ToastType,
    val position: ToastPosition?,
    val durationMillis: Long,
)

class TrovesToastState {
    var current by mutableStateOf<ToastData?>(null)
        private set

    fun show(
        message: String,
        type: ToastType = ToastType.Info,
        position: ToastPosition? = null,
        durationMillis: Long = 2200L,
    ) {
        current = ToastData(message, type, position, durationMillis)
    }

    fun dismiss() {
        current = null
    }
}

@Composable
fun rememberTrovesToastState(): TrovesToastState = remember { TrovesToastState() }

@Composable
fun TrovesToastHost(
    state: TrovesToastState,
    modifier: Modifier = Modifier,
    defaultPosition: ToastPosition = ToastPosition.Top,
) {
    val data = state.current
    var lastData by remember { mutableStateOf(data) }
    if (data != null) lastData = data

    LaunchedEffect(data) {
        if (data != null) {
            delay(data.durationMillis)
            state.dismiss()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        val isTop = (lastData?.position ?: defaultPosition) == ToastPosition.Top
        AnimatedVisibility(
            visible = data != null,
            modifier = Modifier.align(if (isTop) Alignment.TopCenter else Alignment.BottomCenter),
            enter = fadeIn() + slideInVertically { if (isTop) -it else it },
            exit = fadeOut() + slideOutVertically { if (isTop) -it else it },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (isTop) Modifier.statusBarsPadding() else Modifier.navigationBarsPadding())
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                lastData?.let { TrovesToastCard(message = it.message, type = it.type) }
            }
        }
    }
}
