package com.troves.domain.usecase.cart

import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.TrovesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch


class SyncCartUseCase(
    private val cartRepository: TrovesRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    operator fun invoke() {
        CoroutineScope(Dispatchers.IO).launch {
            if (authenticationRepository.isLoggedIn()) {
                runCatching { cartRepository.refreshCart() }
            }
        }
    }
}
