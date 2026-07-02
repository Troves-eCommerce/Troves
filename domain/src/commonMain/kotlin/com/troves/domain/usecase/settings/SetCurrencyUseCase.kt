package com.troves.domain.usecase.settings

import com.troves.domain.repository.UserPreferencesRepository

class SetCurrencyUseCase(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(currency: String) =
        repository.setSelectedCurrency(currency)
}