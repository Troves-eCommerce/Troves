package com.troves.domain.usecase.cart

import com.troves.domain.repository.TrovesRepository

class SetCartHintShownUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(shown: Boolean = true) {
        repository.setCartHintShown(shown)
    }
}
