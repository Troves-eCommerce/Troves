package com.troves.domain.usecase.address

import com.troves.domain.entity.Address
import com.troves.domain.repository.AddressRepository
import com.troves.domain.utils.Result

class AddAddressUseCase(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(address: Address): Result<Address> =
        addressRepository.addAddress(address)
}
