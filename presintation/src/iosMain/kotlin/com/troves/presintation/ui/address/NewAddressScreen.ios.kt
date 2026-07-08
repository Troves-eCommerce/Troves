package com.troves.presintation.ui.address

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

@Composable
actual fun RequestLocationPermissionHandler(
    trigger: Boolean,
    onDismiss: (Boolean) -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
}