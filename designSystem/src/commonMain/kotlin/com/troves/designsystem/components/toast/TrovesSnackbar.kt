package com.troves.designsystem.components.toast

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class TrovesSnackbarVisuals(
    override val message: String,
    val type: ToastType,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
) : SnackbarVisuals {
    override val actionLabel: String? = null
    override val withDismissAction: Boolean = false
}

suspend fun SnackbarHostState.showTroves(
    message: String,
    type: ToastType = ToastType.Info,
) {
    showSnackbar(TrovesSnackbarVisuals(message = message, type = type))
}

@Composable
fun TrovesSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        val type = (data.visuals as? TrovesSnackbarVisuals)?.type ?: ToastType.Info
        TrovesToastCard(message = data.visuals.message, type = type)
    }
}
