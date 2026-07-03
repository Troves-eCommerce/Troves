package com.troves.domain.usecase.shared

import com.troves.domain.utils.Result
import com.troves.domain.repository.TrovesRepository

class GetCitiesUseCase(
    private val trovesRepository: TrovesRepository
) {
    suspend operator fun invoke(countryName: String): Result<List<String>> {
        return trovesRepository.getCities(countryName)
    }
}
