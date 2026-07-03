package com.troves.domain.usecase.shared

import com.troves.domain.utils.Result
import com.troves.domain.repository.LocationRepository

class GetCountriesUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Result<List<String>> {
        return locationRepository.getCountries()
    }
}
