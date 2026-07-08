package com.troves.data.source.remote.service.admin

import com.troves.domain.utils.Result


interface ShopifyAdminCustomerService {
    suspend fun findCustomerIdByEmail(email: String): Long?

    suspend fun setCustomerPassword(customerId: Long, password: String): Result<Unit>
}
