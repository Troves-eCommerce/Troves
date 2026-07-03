package com.troves.data.source.remote.location

import com.troves.data.source.remote.location.dto.CountriesNowCitiesResponse
import com.troves.data.source.remote.location.dto.CountriesNowIsoResponse
import com.troves.data.source.remote.service.ktor.getResults
import com.troves.domain.utils.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol

class LocationApiServiceImpl(
    private val client: HttpClient,
) : LocationApiService {

    override suspend fun getCountries(): Result<CountriesNowIsoResponse> =
        client.getResults {
            method = HttpMethod.Get
            url {
                protocol = URLProtocol.HTTPS
                host = COUNTRIES_NOW_HOST
                pathSegments = listOf("api", "v0.1", "countries", "iso")
            }
            header("Accept", ContentType.Application.Json.toString())
        }

    override suspend fun getCities(country: String): Result<CountriesNowCitiesResponse> =
        client.getResults {
            method = HttpMethod.Get
            url {
                protocol = URLProtocol.HTTPS
                host = COUNTRIES_NOW_HOST
                pathSegments = listOf("api", "v0.1", "countries", "cities", "q")
                parameters.append("country", country)
            }
            header("Accept", ContentType.Application.Json.toString())
        }

    private companion object {
        const val COUNTRIES_NOW_HOST = "countriesnow.space"
    }
}
