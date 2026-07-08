package com.troves.data.repository

import com.troves.data.source.framework.location.datasource.FrameworkLocationDatasource
import com.troves.data.source.remote.location.LocationDataSource
import com.troves.domain.entity.LocationAddress
import com.troves.domain.entity.LocationCoordinates
import com.troves.domain.repository.LocationRepository
import com.troves.domain.utils.Result
import com.troves.domain.utils.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepositoryImpl(
    private val locationDataSource: LocationDataSource,
    private val frameworkLocationDatasource: FrameworkLocationDatasource,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : LocationRepository {

    override suspend fun getCountries(): Result<List<String>> = withContext(coroutineDispatcher) {
        locationDataSource.getCountries().map { response ->
            response.data.orEmpty()
                .mapNotNull { it.name }
                .distinct()
                .sorted()
        }
    }

    override suspend fun getCities(countryName: String): Result<List<String>> =
        withContext(coroutineDispatcher) {
            locationDataSource.getCities(countryName).map { response ->
                response.data.orEmpty().distinct().sorted()
            }
        }

    // Framework Location

    override suspend fun reverseGeocode(
        coordinates: LocationCoordinates
    ): Result<LocationAddress> = withContext(coroutineDispatcher) {
        frameworkLocationDatasource
            .reverseGeocode(
                coordinates = com.troves.data.source.framework.location.service.LocationCoordinates(
                    coordinates.lan,
                    coordinates.lon
                )
            )
            // A Result.Error (e.g. LocationIQ 404 "Unable to geocode") passes
            // straight through — never thrown. A 200 with a null address
            // degrades to EMPTY, handled downstream as "no usable fields".
            .map { response -> response.address?.toDomain() ?: LocationAddress.EMPTY }
    }

    override suspend fun getCurrentLocationCoordinates(): LocationCoordinates =
        frameworkLocationDatasource.getCurrentLocationCoordinates().toDomain()
}
