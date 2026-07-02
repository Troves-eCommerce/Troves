package com.troves.domain.usecase.shared

import com.troves.domain.utils.Result
import com.troves.domain.repository.TrovesRepository

class GetCountriesUseCase(
    private val trovesRepository: TrovesRepository
) {
    suspend operator fun invoke(): Result<List<String>> {
        return trovesRepository.getCountries()
    }
}
