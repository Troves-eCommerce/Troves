package com.troves.domain.usecase.address

import com.troves.domain.entity.Address
import com.troves.domain.repository.AddressRepository

class GetDefaultSavedAddressUseCase(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(): Address? = addressRepository.getDefaultAddress()
}
