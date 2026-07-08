package com.troves.presintation.ui.address

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
actual fun RequestLocationPermissionHandler(
    trigger: Boolean,
    onDismiss: (Boolean) -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val activity = LocalActivity.current
    var showSettingsDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Permission Required") },
            text = { Text("Location permission is permanently denied. Please enable it in settings to use this feature.") },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", "com.troves", null)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }.also {
                        activity?.startActivity(it)
                    }
                }) {
                    Text("Go to Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.all { it }
        onDismiss(isGranted)
        if (!isGranted) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Location permission is required to fetch your current address",
                    withDismissAction = true
                )
            }
            showSettingsDialog = true
        }
    }

    LaunchedEffect(trigger) {
        if (trigger) {
            launcher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}
