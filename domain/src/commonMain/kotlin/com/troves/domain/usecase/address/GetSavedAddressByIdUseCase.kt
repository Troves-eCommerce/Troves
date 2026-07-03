package com.troves.domain.usecase.address

import com.troves.domain.entity.Address
import com.troves.domain.repository.AddressRepository

class GetSavedAddressByIdUseCase(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(id: String): Address? = addressRepository.getAddress(id)
}
