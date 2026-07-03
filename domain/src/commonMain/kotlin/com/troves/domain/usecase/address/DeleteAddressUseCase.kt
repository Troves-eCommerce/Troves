package com.troves.domain.usecase.address

import com.troves.domain.repository.AddressRepository
import com.troves.domain.utils.Result

class DeleteAddressUseCase(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(addressId: String): Result<Unit> =
        addressRepository.deleteAddress(addressId)
}
