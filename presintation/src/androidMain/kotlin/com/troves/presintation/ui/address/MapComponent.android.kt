package com.troves.presintation.ui.address

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.LocationCoordinates
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_location

@Composable
actual fun MapBox(
    selectedLatitude: Double?,
    selectedLongitude: Double?,
    currentLocation: LocationCoordinates,
    flyToCurrentLocationTrigger: Int,
    onMapClick: (latitude: Double, longitude: Double) -> Unit
) {
    val viewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(selectedLongitude ?: 0.0, selectedLatitude ?: 0.0))
            zoom(12.0)
        }
    }
    LaunchedEffect(currentLocation, flyToCurrentLocationTrigger) {
        if (currentLocation.lan != 0.0 && currentLocation.lon != 0.0) {
            if (flyToCurrentLocationTrigger > 0 || (selectedLatitude == null && selectedLongitude == null)) {
                viewportState.flyTo(
                    cameraOptions = CameraOptions.Builder()
                        .center(Point.fromLngLat(currentLocation.lon, currentLocation.lan))
                        .zoom(14.0)
                        .build(),
                    animationOptions = MapAnimationOptions.mapAnimationOptions {
                        duration(2000)
                    }
                )
            }
        }
    }

    LaunchedEffect(selectedLatitude, selectedLongitude) {
        if (selectedLatitude != null && selectedLongitude != null) {
            viewportState.flyTo(
                cameraOptions = CameraOptions.Builder()
                    .center(Point.fromLngLat(selectedLongitude, selectedLatitude))
                    .zoom(14.0)
                    .build(),
                animationOptions = MapAnimationOptions.mapAnimationOptions {
                    duration(1000)
                }
            )
        }
    }

    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = viewportState,
        onMapClickListener = { point ->
            onMapClick(point.latitude(), point.longitude())
            true
        },
    ) {
        if (selectedLatitude != null && selectedLongitude != null) {
            ViewAnnotation(
                options = viewAnnotationOptions {
                    geometry(Point.fromLngLat(selectedLongitude, selectedLatitude))
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_location),
                    contentDescription = "Selected Location",
                    tint = Theme.colors.amber,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}