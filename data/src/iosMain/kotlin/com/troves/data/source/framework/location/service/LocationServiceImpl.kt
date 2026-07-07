package com.troves.data.source.framework.location.service


class LocationServiceImpl: LocationService{
    override suspend fun requestPermission(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentLocationCoordinates(): LocationCoordinates {
        TODO("Not yet implemented")
    }

    override suspend fun reverseGeocode(coordinates: LocationCoordinates): LocationAddress {
        TODO("Not yet implemented")
    }

}