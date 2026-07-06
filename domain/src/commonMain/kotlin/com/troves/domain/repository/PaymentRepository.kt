package com.troves.domain.repository

import com.troves.domain.entity.ClientSecret
import com.troves.domain.utils.Result

interface PaymentRepository {
    suspend fun getClientSecret(cartId: String): Result<ClientSecret>
}
