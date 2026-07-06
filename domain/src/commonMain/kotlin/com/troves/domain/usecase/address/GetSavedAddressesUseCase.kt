package com.troves.domain.usecase.address

import com.troves.domain.entity.Address
import com.troves.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow

class GetSavedAddressesUseCase(
    private val addressRepository: AddressRepository
) {
    operator fun invoke(): Flow<List<Address>> {
        return addressRepository.addresses
    }
}
