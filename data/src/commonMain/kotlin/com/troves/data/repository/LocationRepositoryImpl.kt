package com.troves.data.repository

import com.troves.data.source.remote.location.LocationDataSource
import com.troves.domain.repository.LocationRepository
import com.troves.domain.utils.Result
import com.troves.domain.utils.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepositoryImpl(
    private val locationDataSource: LocationDataSource,
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

    override suspend fun getCities(countryName: String): Result<List<String>> = withContext(coroutineDispatcher) {
        locationDataSource.getCities(countryName).map { response ->
            response.data.orEmpty().distinct().sorted()
        }
    }
}
