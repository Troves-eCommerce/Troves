package com.troves.data.repository

import com.troves.data.source.remote.location.MapboxSearchApiService
import com.troves.domain.entity.LocationAddress
import com.troves.domain.entity.LocationCoordinates
import com.troves.domain.repository.MapboxLocationDetails
import com.troves.domain.repository.MapboxSearchRepository
import com.troves.domain.repository.MapboxSuggestionModel
import com.troves.domain.utils.Result
import com.troves.domain.utils.map

class MapboxSearchRepositoryImpl(
    private val apiService: MapboxSearchApiService
) : MapboxSearchRepository {
    private var currentSessionToken: String = generateSessionToken()

    override suspend fun getSuggestions(query: String): Result<List<MapboxSuggestionModel>> {
        return apiService.suggest(query, currentSessionToken).map { response ->
            response.suggestions.map {
                MapboxSuggestionModel(
                    id = it.mapboxId,
                    name = it.name,
                    formattedAddress = it.placeFormatted.ifBlank { it.fullAddress }
                )
            }
        }
    }

    override suspend fun retrieveDetails(mapboxId: String): Result<MapboxLocationDetails> {
        return apiService.retrieve(mapboxId, currentSessionToken).map { response ->
            val feature = response.features.firstOrNull()
                ?: throw Exception("No feature found for this mapboxId")
                
            val properties = feature.properties
            val coords = feature.geometry.coordinates
            if (coords.size < 2) throw Exception("Invalid coordinates")

            val locationCoords = LocationCoordinates(lon = coords[0], lan = coords[1])
            
            MapboxLocationDetails(
                name = properties.name,
                coordinates = locationCoords,
                address = LocationAddress(
                    houseNumber = "",
                    road = properties.name,
                    neighbourhood = "",
                    suburb = "",
                    city = "",
                    state = "",
                    country = "",
                    countryCode = "",
                    postcode = ""
                )
            )
        }.also {
            if (it is Result.Success) {
                resetSessionToken()
            }
        }
    }

    override fun resetSessionToken() {
        currentSessionToken = generateSessionToken()
    }

    private fun generateSessionToken(): String {
        val chars = "0123456789abcdef"
        return (1..32).map { chars.random() }.joinToString("")
    }
}
