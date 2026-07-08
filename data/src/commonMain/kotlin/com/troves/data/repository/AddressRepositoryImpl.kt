package com.troves.data.repository

import com.troves.data.mapper.applyDecoration
import com.troves.data.mapper.toDecorationEntity
import com.troves.data.source.local.database.AddressDao
import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.service.StorefrontApiService
import com.troves.domain.entity.Address
import com.troves.domain.repository.AddressRepository
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.utils.Result
import com.troves.domain.utils.ShopifyAuthRequiredException
import com.troves.domain.utils.ShopifyProvisioningFailedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first


class AddressRepositoryImpl(
    private val storefront: StorefrontApiService,
    private val preferences: TrovesPreferences,
    private val addressDao: AddressDao,
    private val authRepository: AuthenticationRepository,
) : AddressRepository {

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    override val addresses: Flow<List<Address>> = _addresses.asStateFlow()


    private suspend fun requireToken(): Result<String> {
        preferences.shopifyCustomerAccessTokenOrNull.first()?.takeIf { it.isNotBlank() }
            ?.let { return Result.Success(it) }

        return when (val healed = authRepository.ensureShopifyToken()) {
            is Result.Success -> {
                val token = preferences.shopifyCustomerAccessTokenOrNull.first()
                if (token.isNullOrBlank()) Result.Error(ShopifyProvisioningFailedException())
                else Result.Success(token)
            }
            is Result.Error -> Result.Error(healed.throwable)
            is Result.Loading -> Result.Loading
        }
    }

    private suspend fun <T> withToken(block: suspend (token: String) -> Result<T>): Result<T> {
        val token = when (val t = requireToken()) {
            is Result.Success -> t.value
            is Result.Error -> return Result.Error(t.throwable)
            is Result.Loading -> return Result.Loading
        }
        val first = block(token)
        if (first is Result.Error && isShopifyAuthError(first.throwable)) {
            preferences.clearShopifyCustomerAccessToken()
            return when (val t2 = requireToken()) {
                is Result.Success -> block(t2.value)
                is Result.Error -> Result.Error(t2.throwable)
                is Result.Loading -> Result.Loading
            }
        }
        return first
    }

    private fun isShopifyAuthError(t: Throwable): Boolean {
        val m = t.message?.lowercase() ?: return false
        return "token" in m || "unidentified" in m || "identify the customer" in m ||
            "logged in" in m || "expired" in m || "access denied" in m
    }

    override suspend fun refresh(): Result<Unit> = withToken { token ->
        when (val res = storefront.getCustomerAddresses(token)) {
            is Result.Success -> {
                val decorations = addressDao.getDecorations().associateBy { it.addressId }
                _addresses.value = res.value.map { it.applyDecoration(decorations[it.id]) }
                Result.Success(Unit)
            }
            is Result.Error -> res
            is Result.Loading -> Result.Loading
        }
    }.also {
        // Only wipe the cached list when the session itself is invalid — not on a
        // transient network error, so the user keeps seeing their addresses.
        if (it is Result.Error &&
            (it.throwable is ShopifyAuthRequiredException || it.throwable is ShopifyProvisioningFailedException)
        ) _addresses.value = emptyList()
    }

    override suspend fun addAddress(address: Address): Result<Address> = withToken { token ->
        when (val res = storefront.createCustomerAddress(token, address)) {
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

    override suspend fun updateAddress(address: Address): Result<Address> = withToken { token ->
        when (val res = storefront.updateCustomerAddress(token, address.id, address)) {
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

    override suspend fun deleteAddress(addressId: String): Result<Unit> = withToken { token ->
        when (val res = storefront.deleteCustomerAddress(token, addressId)) {
            is Result.Success -> {
                addressDao.deleteDecoration(addressId)
                refresh()
                Result.Success(Unit)
            }
            is Result.Error -> res
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun setDefaultAddress(addressId: String): Result<Unit> = withToken { token ->
        when (val res = storefront.setDefaultCustomerAddress(token, addressId)) {
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
