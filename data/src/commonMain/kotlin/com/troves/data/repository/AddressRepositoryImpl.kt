package com.troves.data.repository

import com.troves.data.source.local.database.AddressDao
import com.troves.data.mapper.toDomain
import com.troves.data.mapper.toEntity
import com.troves.domain.entity.Address
import com.troves.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AddressRepositoryImpl(
    private val addressDao: AddressDao
) : AddressRepository {

    override val addresses: Flow<List<Address>> = addressDao.getAddresses().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun addAddress(address: Address) {
        addressDao.insertAddress(address.toEntity())
    }

    override suspend fun deleteAddress(addressId: String) {
        addressDao.deleteAddress(addressId)
    }

    override suspend fun updateAddress(address: Address) {
        addressDao.updateAddress(address.toEntity())
    }
}
