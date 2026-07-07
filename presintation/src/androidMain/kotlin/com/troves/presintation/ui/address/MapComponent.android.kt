package com.troves.presintation.ui.address

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_location

@Composable
actual fun MapBox(
    selectedLatitude: Double?,
    selectedLongitude: Double?,
    onMapClick: (latitude: Double, longitude: Double) -> Unit
) {
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                center(Point.fromLngLat(31.2357, 30.0444))
                zoom(12.0)
            }
        },
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