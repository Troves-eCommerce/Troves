package com.troves.domain.usecase.settings

import com.troves.domain.entity.ExchangeRate
import com.troves.domain.repository.CurrencyRepository

class FetchLatestRatesUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(base: String = "EGP"): Result<ExchangeRate> =
        repository.fetchLatestRates(base)
}
