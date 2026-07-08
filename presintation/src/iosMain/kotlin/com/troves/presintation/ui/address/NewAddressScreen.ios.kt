package com.troves.presintation.ui.address

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.darwin.NSObject

@Composable
actual fun RequestLocationPermissionHandler(
    trigger: Boolean,
    onDismiss: (Boolean) -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    val currentTrigger by rememberUpdatedState(trigger)
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val currentScope by rememberUpdatedState(scope)
    val currentSnackbarHostState by rememberUpdatedState(snackbarHostState)

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Permission Required") },
            text = { Text("Location permission is permanently denied. Please enable it in settings to use this feature.") },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
                    if (url != null) {
                        UIApplication.sharedApplication.openURL(url)
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

    val locationManager = remember { CLLocationManager() }

    val delegate = remember {
        object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                if (currentTrigger) {
                    val status = manager.authorizationStatus
                    if (status != kCLAuthorizationStatusNotDetermined) {
                        val isGranted = status == kCLAuthorizationStatusAuthorizedWhenInUse || 
                                        status == kCLAuthorizationStatusAuthorizedAlways
                        currentOnDismiss(isGranted)
                        if (!isGranted) {
                            currentScope.launch {
                                currentSnackbarHostState.showSnackbar(
                                    message = "Location permission is required to fetch your current address",
                                    withDismissAction = true
                                )
                            }
                            showSettingsDialog = true
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        locationManager.delegate = delegate
    }

    LaunchedEffect(trigger) {
        if (trigger) {
            val status = locationManager.authorizationStatus
            if (status == kCLAuthorizationStatusNotDetermined) {
                locationManager.requestWhenInUseAuthorization()
            } else {
                val isGranted = status == kCLAuthorizationStatusAuthorizedWhenInUse || 
                                status == kCLAuthorizationStatusAuthorizedAlways
                currentOnDismiss(isGranted)
                if (!isGranted) {
                    currentScope.launch {
                        currentSnackbarHostState.showSnackbar(
                            message = "Location permission is required to fetch your current address",
                            withDismissAction = true
                        )
                    }
                    showSettingsDialog = true
                }
            }
        }
    }
}