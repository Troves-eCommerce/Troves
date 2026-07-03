package com.troves.domain.usecase.address

import com.troves.domain.repository.AddressRepository

class DeleteAddressUseCase(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(addressId: String) {
        addressRepository.deleteAddress(addressId)
    }
}
