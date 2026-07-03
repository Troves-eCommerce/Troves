package com.troves.domain.usecase.address

import com.troves.domain.repository.AddressRepository
import com.troves.domain.utils.Result

class RefreshAddressesUseCase(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(): Result<Unit> = addressRepository.refresh()
}
