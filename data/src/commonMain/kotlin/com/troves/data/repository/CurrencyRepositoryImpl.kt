package com.troves.data.repository

import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.CurrencyRemoteDataSource
import com.troves.domain.entity.ExchangeRate
import com.troves.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CurrencyRepositoryImpl(
    private val remoteDataSource: CurrencyRemoteDataSource,
    private val preferences: TrovesPreferences
) : CurrencyRepository {

    override suspend fun fetchLatestRates(baseCurrency: String): Result<ExchangeRate> {
        return try {
            val rates = remoteDataSource.getLatestRates(baseCurrency)
            saveRates(rates)
            Result.success(rates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSavedRates(): Flow<ExchangeRate?> {
        return preferences.exchangeRatesJson.map { json ->
            json?.let {
                try {
                    Json.decodeFromString<ExchangeRate>(it)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override suspend fun saveRates(exchangeRate: ExchangeRate) {
        val json = Json.encodeToString(exchangeRate)
        preferences.setExchangeRatesJson(json)
    }
}
