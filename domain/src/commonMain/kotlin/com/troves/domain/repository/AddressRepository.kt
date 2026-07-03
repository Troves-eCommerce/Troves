package com.troves.domain.repository

import com.troves.domain.entity.Address
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    val addresses: Flow<List<Address>>
    suspend fun addAddress(address: Address)
    suspend fun deleteAddress(addressId: String)
    suspend fun updateAddress(address: Address)
}
