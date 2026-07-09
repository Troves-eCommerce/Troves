package com.troves.domain.usecase.address

import com.troves.domain.repository.MapboxSearchRepository
import com.troves.domain.repository.MapboxSuggestionModel
import com.troves.domain.utils.Result

class GetMapboxSuggestionsUseCase(
    private val mapboxSearchRepository: MapboxSearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<MapboxSuggestionModel>> {
        return mapboxSearchRepository.getSuggestions(query)
    }
}
