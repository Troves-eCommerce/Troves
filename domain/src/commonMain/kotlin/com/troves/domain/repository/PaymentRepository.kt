package com.troves.domain.repository

import com.troves.domain.entity.ClientSecret

interface PaymentRepository {
    suspend fun getClientSecret(cartId: String): ClientSecret
}