package com.troves.domain.repository

import com.troves.domain.entity.Address
import com.troves.domain.utils.Result
import kotlinx.coroutines.flow.Flow


interface AddressRepository {
    val addresses: Flow<List<Address>>

    suspend fun refresh(): Result<Unit>

    suspend fun addAddress(address: Address): Result<Address>
    suspend fun updateAddress(address: Address): Result<Address>
    suspend fun deleteAddress(addressId: String): Result<Unit>
    suspend fun setDefaultAddress(addressId: String): Result<Unit>

    suspend fun getDefaultAddress(): Address?
    suspend fun getAddress(id: String): Address?
}
