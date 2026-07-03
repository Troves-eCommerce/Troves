package com.troves.data.source.remote.location

import com.troves.data.source.remote.location.dto.CountriesNowCitiesResponse
import com.troves.data.source.remote.location.dto.CountriesNowIsoResponse
import com.troves.domain.utils.Result

interface LocationApiService {
    suspend fun getCountries(): Result<CountriesNowIsoResponse>
    suspend fun getCities(country: String): Result<CountriesNowCitiesResponse>
}
