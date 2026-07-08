package com.troves.domain.repository

import com.troves.domain.entity.LocationAddress
import com.troves.domain.entity.LocationCoordinates
import com.troves.domain.utils.Result

interface LocationRepository {
    suspend fun getCountries(): Result<List<String>>
    suspend fun getCities(countryName: String): Result<List<String>>

    suspend fun reverseGeocode(coordinates: LocationCoordinates): Result<LocationAddress>
    suspend fun getCurrentLocationCoordinates(): LocationCoordinates
}
