package com.troves.presintation.ui.address

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

@Composable
actual fun MapBox() {
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                center(Point.fromLngLat(25.004, 60.239))
                zoom(9.0)
            }
        }
    )
}