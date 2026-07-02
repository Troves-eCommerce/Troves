package com.troves.domain.usecase.cart

import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.CartRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class SyncCartUseCase(
    private val cartRepository: CartRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    operator fun invoke() {
        CoroutineScope(Dispatchers.IO).launch {
            if (authenticationRepository.isLoggedIn()) {
                val userId = authenticationRepository.getCurrentUserId() ?: return@launch
                cartRepository.syncFromRemote(userId)
                cartRepository.syncLocalOfflineCart(userId)
            }
        }
    }
}
