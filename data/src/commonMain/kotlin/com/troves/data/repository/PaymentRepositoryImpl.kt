package com.troves.data.repository

import com.troves.data.source.remote.service.paymob.PaymobApiService
import com.troves.data.source.remote.service.paymob.toDomain
import com.troves.domain.entity.ClientSecret
import com.troves.domain.repository.PaymentRepository
import com.troves.domain.utils.Result
import com.troves.domain.utils.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class PaymentRepositoryImpl(
    private val paymobApiService: PaymobApiService,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PaymentRepository {
    override suspend fun getClientSecret(cartId: String): Result<ClientSecret> {
        return withContext(
            coroutineDispatcher
        ) {
            paymobApiService.getClientSecret(cartId = cartId).map { it.toDomain() }
        }

    }
}