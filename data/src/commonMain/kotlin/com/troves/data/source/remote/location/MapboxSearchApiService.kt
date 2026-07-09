package com.troves.data.source.remote.location

import com.troves.data.source.remote.location.dto.MapboxRetrieveResponse
import com.troves.data.source.remote.location.dto.MapboxSuggestResponse
import com.troves.domain.utils.Result

interface MapboxSearchApiService {
    suspend fun suggest(query: String, sessionToken: String): Result<MapboxSuggestResponse>
    suspend fun retrieve(mapboxId: String, sessionToken: String): Result<MapboxRetrieveResponse>
}
