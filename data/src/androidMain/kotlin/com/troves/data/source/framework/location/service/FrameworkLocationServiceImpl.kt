package com.troves.data.source.framework.location.service

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.troves.data.util.AndroidApp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await


class FrameworkLocationServiceImpl : FrameworkLocationService {
    private fun requestPermission(): Boolean {
        return listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ).map {
            ContextCompat.checkSelfPermission(AndroidApp.androidApp, it)
        }.all {
            it == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    override suspend fun getCurrentLocationCoordinates(): LocationCoordinates {
        if (!requestPermission()) {
            return LocationCoordinates()
        }

        return try {
            val provider =
                LocationServices.getFusedLocationProviderClient(AndroidApp.androidApp)

            val location = provider
                .getCurrentLocation(
                    CurrentLocationRequest.Builder().build(),
                    CancellationTokenSource().token
                )
                .await()
            location?.toCoordinates() ?: LocationCoordinates()
        } catch (c: CancellationException) {
            throw c
        } catch (t: Throwable) {
            LocationCoordinates()
        }
    }
}


fun Location.toCoordinates(): LocationCoordinates = LocationCoordinates(latitude, longitude)