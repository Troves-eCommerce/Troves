package com.troves.domain.repository

import com.troves.domain.utils.Result

interface LocationRepository {
    suspend fun getCountries(): Result<List<String>>
    suspend fun getCities(countryName: String): Result<List<String>>
}
