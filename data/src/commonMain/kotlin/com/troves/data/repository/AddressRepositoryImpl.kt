package com.troves.data.repository

import com.troves.data.mapper.applyDecoration
import com.troves.data.mapper.toDecorationEntity
import com.troves.data.source.local.database.AddressDao
import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.service.StorefrontApiService
import com.troves.domain.entity.Address
import com.troves.domain.repository.AddressRepository
import com.troves.domain.utils.Result
import com.troves.domain.utils.ShopifyAuthRequiredException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first


class AddressRepositoryImpl(
    private val storefront: StorefrontApiService,
    private val preferences: TrovesPreferences,
    private val addressDao: AddressDao,
) : AddressRepository {

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    override val addresses: Flow<List<Address>> = _addresses.asStateFlow()

    private suspend fun requireToken(): Result<String> {
        val token = preferences.shopifyCustomerAccessTokenOrNull.first()
        return if (token.isNullOrBlank()) {
            Result.Error(ShopifyAuthRequiredException())
        } else {
            Result.Success(token)
        }
    }

    override suspend fun refresh(): Result<Unit> {
        val token = when (val t = requireToken()) {
            is Result.Success -> t.value
            is Result.Error -> { _addresses.value = emptyList(); return t }
            is Result.Loading -> return Result.Loading
        }
        return when (val res = storefront.getCustomerAddresses(token)) {
            is Result.Success -> {
                val decorations = addressDao.getDecorations().associateBy { it.addressId }
                _addresses.value = res.value.map { it.applyDecoration(decorations[it.id]) }
                Result.Success(Unit)
            }
            is Result.Error -> res
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun addAddress(address: Address): Result<Address> {
        val token = when (val t = requireToken()) {
            is Result.Success -> t.value
            is Result.Error -> return t
            is Result.Loading -> return Result.Loading
        }
        return when (val res = storefront.createCustomerAddress(token, address)) {
            is Result.Success -> {
                val created = res.value
                addressDao.upsertDecoration(address.copy(id = created.id).toDecorationEntity())
                if (address.isDefault) storefront.setDefaultCustomerAddress(token, created.id)
                refresh()
                Result.Success(created)
            }
            is Result.Error -> res
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun updateAddress(address: Address): Result<Address> {
        val token = when (val t = requireToken()) {
            is Result.Success -> t.value
            is Result.Error -> return t
            is Result.Loading -> return Result.Loading
        }
        return when (val res = storefront.updateCustomerAddress(token, address.id, address)) {
            is Result.Success -> {
                addressDao.upsertDecoration(address.toDecorationEntity())
                if (address.isDefault) storefront.setDefaultCustomerAddress(token, address.id)
                refresh()
                Result.Success(res.value)
            }
            is Result.Error -> res
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun deleteAddress(addressId: String): Result<Unit> {
        val token = when (val t = requireToken()) {
            is Result.Success -> t.value
            is Result.Error -> return t
            is Result.Loading -> return Result.Loading
        }
        return when (val res = storefront.deleteCustomerAddress(token, addressId)) {
            is Result.Success -> {
                addressDao.deleteDecoration(addressId)
                refresh()
                Result.Success(Unit)
            }
            is Result.Error -> res
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun setDefaultAddress(addressId: String): Result<Unit> {
        val token = when (val t = requireToken()) {
            is Result.Success -> t.value
            is Result.Error -> return t
            is Result.Loading -> return Result.Loading
        }
        return when (val res = storefront.setDefaultCustomerAddress(token, addressId)) {
            is Result.Success -> { refresh(); Result.Success(Unit) }
            is Result.Error -> res
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun getDefaultAddress(): Address? {
        if (_addresses.value.isEmpty()) refresh()
        return _addresses.value.firstOrNull { it.isDefault } ?: _addresses.value.firstOrNull()
    }

    override suspend fun getAddress(id: String): Address? {
        if (_addresses.value.isEmpty()) refresh()
        return _addresses.value.firstOrNull { it.id == id }
    }
}
