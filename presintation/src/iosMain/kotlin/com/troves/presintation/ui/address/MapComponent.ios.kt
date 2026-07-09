@file:OptIn(ExperimentalForeignApi::class)
package com.troves.presintation.ui.address

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.troves.domain.entity.LocationCoordinates
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSSelectorFromString
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.UIKit.UIGestureRecognizerStateRecognized
import platform.UIKit.UITapGestureRecognizer
import platform.darwin.NSObject

@Composable
actual fun MapBox(
    selectedLatitude: Double?,
    selectedLongitude: Double?,
    currentLocation: LocationCoordinates,
    flyToCurrentLocationTrigger: Int,
    onMapClick: (latitude: Double, longitude: Double) -> Unit
) {
    val tapDelegate = remember {
        MapTapDelegate(onMapClick)
    }

    val mkMapView = remember { 
        MKMapView().apply {
            showsUserLocation = true
            val tapGesture = UITapGestureRecognizer(target = tapDelegate, action = NSSelectorFromString("handleTap:"))
            addGestureRecognizer(tapGesture)
            tapDelegate.mapView = this
        } 
    }
    
    LaunchedEffect(currentLocation, flyToCurrentLocationTrigger) {
        if (currentLocation.lan != 0.0 && currentLocation.lon != 0.0) {
            if (flyToCurrentLocationTrigger > 0 || (selectedLatitude == null && selectedLongitude == null)) {
                val center = CLLocationCoordinate2DMake(currentLocation.lan, currentLocation.lon)
                val region = MKCoordinateRegionMakeWithDistance(center, 10000.0, 10000.0)
                mkMapView.setRegion(region, animated = true)
            }
        }
    }

    LaunchedEffect(selectedLatitude, selectedLongitude) {
        if (selectedLatitude != null && selectedLongitude != null) {
            val center = CLLocationCoordinate2DMake(selectedLatitude, selectedLongitude)
            val region = MKCoordinateRegionMakeWithDistance(center, 10000.0, 10000.0)
            mkMapView.setRegion(region, animated = true)
        }
    }

    UIKitView(
        factory = {
            mkMapView
        },
        modifier = Modifier.fillMaxSize(),
        update = { mapView ->
            mapView.removeAnnotations(mapView.annotations)
            if (selectedLatitude != null && selectedLongitude != null) {
                val annotation = MKPointAnnotation()
                annotation.setCoordinate(CLLocationCoordinate2DMake(selectedLatitude, selectedLongitude))
                mapView.addAnnotation(annotation)
            }
        }
    )
}

class MapTapDelegate(
    private val onMapClick: (Double, Double) -> Unit
) : NSObject() {
    var mapView: MKMapView? = null

    @OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
    @ObjCAction
    fun handleTap(sender: UITapGestureRecognizer) {
        val map = mapView ?: return
        if (sender.state == UIGestureRecognizerStateRecognized) {
            val locationInView = sender.locationInView(map)
            val coordinate = map.convertPoint(locationInView, toCoordinateFromView = map)
            coordinate.useContents {
                onMapClick(latitude, longitude)
            }
        }
    }
}