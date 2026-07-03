package com.troves.data.source.remote.location

import com.troves.data.source.remote.location.dto.CountriesNowCitiesResponse
import com.troves.data.source.remote.location.dto.CountriesNowIsoResponse
import com.troves.domain.utils.Result

interface LocationDataSource {
    suspend fun getCountries(): Result<CountriesNowIsoResponse>
    suspend fun getCities(country: String): Result<CountriesNowCitiesResponse>
}

class LocationDataSourceImpl(
    private val locationApiService: LocationApiService,
) : LocationDataSource {
    override suspend fun getCountries(): Result<CountriesNowIsoResponse> =
        locationApiService.getCountries()

    override suspend fun getCities(country: String): Result<CountriesNowCitiesResponse> =
        locationApiService.getCities(country)
}
