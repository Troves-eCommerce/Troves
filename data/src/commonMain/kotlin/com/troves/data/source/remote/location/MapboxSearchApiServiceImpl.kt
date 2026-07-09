package com.troves.data.source.remote.location

import com.troves.data.source.remote.location.dto.MapboxRetrieveResponse
import com.troves.data.source.remote.location.dto.MapboxSuggestResponse
import com.troves.data.source.remote.service.ktor.getResults
import com.troves.domain.utils.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol

class MapboxSearchApiServiceImpl(
    private val client: HttpClient,
    private val accessToken: String
) : MapboxSearchApiService {

    override suspend fun suggest(query: String, sessionToken: String): Result<MapboxSuggestResponse> =
        client.getResults {
            method = HttpMethod.Get
            url {
                protocol = URLProtocol.HTTPS
                host = MAPBOX_HOST
                pathSegments = listOf("search", "searchbox", "v1", "suggest")
                parameters.append("q", query)
                parameters.append("access_token", accessToken)
                parameters.append("session_token", sessionToken)
                parameters.append("limit", "5")
            }
            header("Accept", ContentType.Application.Json.toString())
        }

    override suspend fun retrieve(mapboxId: String, sessionToken: String): Result<MapboxRetrieveResponse> =
        client.getResults {
            method = HttpMethod.Get
            url {
                protocol = URLProtocol.HTTPS
                host = MAPBOX_HOST
                pathSegments = listOf("search", "searchbox", "v1", "retrieve", mapboxId)
                parameters.append("access_token", accessToken)
                parameters.append("session_token", sessionToken)
            }
            header("Accept", ContentType.Application.Json.toString())
        }

    private companion object {
        const val MAPBOX_HOST = "api.mapbox.com"
    }
}
