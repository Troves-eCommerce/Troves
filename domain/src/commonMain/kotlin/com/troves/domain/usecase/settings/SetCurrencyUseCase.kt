package com.troves.domain.usecase.settings

import com.troves.domain.repository.TrovesRepository

class SetCurrencyUseCase(
    private val repository: TrovesRepository
) {
    suspend operator fun invoke(currency: String) =
        repository.setSelectedCurrency(currency)
}