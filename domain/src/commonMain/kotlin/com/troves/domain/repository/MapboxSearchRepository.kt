package com.troves.domain.repository

import com.troves.domain.entity.LocationAddress
import com.troves.domain.entity.LocationCoordinates
import com.troves.domain.utils.Result

interface MapboxSearchRepository {
    suspend fun getSuggestions(query: String): Result<List<MapboxSuggestionModel>>
    suspend fun retrieveDetails(mapboxId: String): Result<MapboxLocationDetails>
    fun resetSessionToken()
}

data class MapboxSuggestionModel(
    val id: String,
    val name: String,
    val formattedAddress: String
)

data class MapboxLocationDetails(
    val name: String,
    val coordinates: LocationCoordinates,
    val address: LocationAddress?
)
