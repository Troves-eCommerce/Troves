package com.troves.domain.usecase.settings

import com.troves.domain.entity.ExchangeRate
import com.troves.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow

class GetExchangeRatesUseCase(
    private val repository: CurrencyRepository
) {
    operator fun invoke(): Flow<ExchangeRate?> = repository.getSavedRates()
}
