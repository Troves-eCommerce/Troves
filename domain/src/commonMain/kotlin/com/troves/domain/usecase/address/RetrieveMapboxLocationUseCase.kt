package com.troves.domain.usecase.address

import com.troves.domain.repository.MapboxLocationDetails
import com.troves.domain.repository.MapboxSearchRepository
import com.troves.domain.utils.Result

class RetrieveMapboxLocationUseCase(
    private val mapboxSearchRepository: MapboxSearchRepository
) {
    suspend operator fun invoke(mapboxId: String): Result<MapboxLocationDetails> {
        return mapboxSearchRepository.retrieveDetails(mapboxId)
    }
}
