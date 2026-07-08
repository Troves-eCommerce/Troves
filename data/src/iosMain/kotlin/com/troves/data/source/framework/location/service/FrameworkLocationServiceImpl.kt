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

class FrameworkLocationServiceImpl: FrameworkLocationService {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getCurrentLocationCoordinates(): LocationCoordinates = suspendCancellableCoroutine { continuation ->
        val locationManager = CLLocationManager()
        
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val location = didUpdateLocations.lastOrNull() as? CLLocation
                if (location != null) {
                    val lat = location.coordinate.useContents { latitude }
                    val lon = location.coordinate.useContents { longitude }
                    manager.stopUpdatingLocation()
                    manager.delegate = null
                    if (continuation.isActive) {
                        continuation.resume(LocationCoordinates(lan = lat, lon = lon))
                    }
                }
            }

            override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                manager.stopUpdatingLocation()
                manager.delegate = null
                if (continuation.isActive) {
                    continuation.resume(LocationCoordinates(0.0, 0.0))
                }
            }
        }
        
        locationManager.delegate = delegate
        locationManager.startUpdatingLocation()

        continuation.invokeOnCancellation {
            locationManager.stopUpdatingLocation()
            locationManager.delegate = null
            val keepDelegate = delegate
        }
    }
}