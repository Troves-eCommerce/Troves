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

class LocationServiceImpl : LocationService {
    lateinit var locationPermissionHandler: LocationPermissionHandler

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun getCurrentLocationCoordinates(): LocationCoordinates {
        if (requestLocationPermission()){
            val provider = LocationServices.getFusedLocationProviderClient(AndroidApp.androidApp)
            val request = CurrentLocationRequest.Builder().build()
            val location = provider.getCurrentLocation(
                request,
                CancellationTokenSource().token
            ).result.toCoordinates()
            locationPermissionHandler.onGranted(location = location)


        }


    }

    override fun getAddressFromCoordinates(locationCoordinates: LocationCoordinates): LocationAddress {
        TODO("Not yet implemented")
    }


    fun requestLocationPermission(): Boolean {
        return listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ).map {
            ContextCompat.checkSelfPermission(AndroidApp.androidApp, it)
        }.all {
            it == PackageManager.PERMISSION_GRANTED
        }
    }

}


fun Location.toCoordinates(): LocationCoordinates = LocationCoordinates(latitude, longitude)