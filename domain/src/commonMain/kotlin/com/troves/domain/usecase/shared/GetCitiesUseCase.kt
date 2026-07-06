package com.troves.domain.usecase.shared

import com.troves.domain.utils.Result
import com.troves.domain.repository.LocationRepository

class GetCitiesUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(countryName: String): Result<List<String>> {
        return locationRepository.getCities(countryName)
    }
}
