package com.troves.data.source.remote

import com.troves.domain.entity.ExchangeRate
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class CurrencyRemoteDataSource(private val httpClient: HttpClient) {
    suspend fun getLatestRates(base: String): ExchangeRate {
        return httpClient.get("https://api.exchangerate-api.com/v4/latest/$base").body<ExchangeRate>()
    }
}
