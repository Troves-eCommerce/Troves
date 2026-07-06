package com.troves.domain.repository

import com.troves.domain.entity.ExchangeRate
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    suspend fun fetchLatestRates(baseCurrency: String): Result<ExchangeRate>
    fun getSavedRates(): Flow<ExchangeRate?>
    suspend fun saveRates(exchangeRate: ExchangeRate)
}
