package com.troves.data.source.framework.location.service

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

class FrameworkLocationServiceImpl : FrameworkLocationService {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getCurrentLocationCoordinates(): LocationCoordinates =
        suspendCancellableCoroutine { continuation ->
            val locationManager = CLLocationManager()

            fun finish(coordinates: LocationCoordinates) {
                if (continuation.isActive) {
                    locationManager.delegate = null
                    continuation.resume(coordinates)
                }
            }

            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(
                    manager: CLLocationManager,
                    didUpdateLocations: List<*>
                ) {
                    val location = didUpdateLocations.lastOrNull() as? CLLocation
                    if (location != null) {
                        val lat = location.coordinate.useContents { latitude }
                        val lon = location.coordinate.useContents { longitude }
                        finish(LocationCoordinates(lan = lat, lon = lon))
                    } else {
                        finish(LocationCoordinates())
                    }
                }

                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: NSError
                ) {
                    finish(LocationCoordinates())
                }
            }

            locationManager.delegate = delegate
            locationManager.requestLocation()

            continuation.invokeOnCancellation {
                locationManager.delegate = null
            }
        }
}
