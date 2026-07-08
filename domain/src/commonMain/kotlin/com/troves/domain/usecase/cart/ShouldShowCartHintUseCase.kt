package com.troves.domain.usecase.cart

import com.troves.domain.repository.TrovesRepository
import kotlinx.coroutines.flow.Flow

class ShouldShowCartHintUseCase(
    private val repository: TrovesRepository,
) {
    operator fun invoke(): Flow<Boolean> = repository.isCartHintShown
}
