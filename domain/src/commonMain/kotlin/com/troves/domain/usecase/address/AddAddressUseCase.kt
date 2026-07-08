package com.troves.domain.usecase.address

import com.troves.domain.entity.Address
import com.troves.domain.repository.AddressRepository
import com.troves.domain.utils.NoConnectionException
import com.troves.domain.utils.Result
import com.troves.domain.utils.connectivity.ConnectivityObserver
import com.troves.domain.utils.connectivity.ConnectivityStatus

class AddAddressUseCase(
    private val addressRepository: AddressRepository,
    private val connectivityObserver: ConnectivityObserver,
) {
    suspend operator fun invoke(address: Address): Result<Address> {
        if (connectivityObserver.currentStatus() != ConnectivityStatus.Available) {
            return Result.Error(NoConnectionException())
        }
        return addressRepository.addAddress(address)
    }
}
