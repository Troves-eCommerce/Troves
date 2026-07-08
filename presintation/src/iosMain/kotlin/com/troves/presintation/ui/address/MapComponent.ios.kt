package com.troves.presintation.ui.address

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.troves.domain.entity.LocationCoordinates
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.UIKit.UIGestureRecognizerStateRecognized
import platform.UIKit.UITapGestureRecognizer
import platform.darwin.NSObject
import kotlinx.cinterop.ObjCAction

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapBox(
    selectedLatitude: Double?,
    selectedLongitude: Double?,
    currentLocation: LocationCoordinates,
    onMapClick: (latitude: Double, longitude: Double) -> Unit
) {
    val tapDelegate = remember {
        MapTapDelegate(onMapClick)
    }

    val mkMapView = remember { 
        MKMapView().apply {
            showsUserLocation = true
            val tapGesture = UITapGestureRecognizer(target = tapDelegate, action = kotlinx.cinterop.NSSelectorFromString("handleTap:"))
            addGestureRecognizer(tapGesture)
            tapDelegate.mapView = this
        } 
    }

    UIKitView(
        factory = {
            mkMapView
        },
        modifier = Modifier.fillMaxSize(),
        update = { mapView ->
            val center = CLLocationCoordinate2DMake(currentLocation.lan, currentLocation.lon)
            val region = MKCoordinateRegionMakeWithDistance(center, 10000.0, 10000.0)
            mapView.setRegion(region, animated = true)

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

    @ObjCAction
    fun handleTap(sender: UITapGestureRecognizer) {
        val map = mapView ?: return
        if (sender.state == UIGestureRecognizerStateRecognized) {
            val locationInView = sender.locationInView(map)
            val coordinate = map.convertPoint(locationInView, toCoordinateFromView = map)
            onMapClick(coordinate.latitude, coordinate.longitude)
        }
    }
}