package com.troves.presintation.ui.address

import androidx.compose.runtime.Composable
import com.troves.domain.entity.LocationCoordinates

@Composable
actual fun MapBox(
    selectedLatitude: Double?,
    selectedLongitude: Double?,
    currentLocation: LocationCoordinates,
    onMapClick: (latitude: Double, longitude: Double) -> Unit
) {

}