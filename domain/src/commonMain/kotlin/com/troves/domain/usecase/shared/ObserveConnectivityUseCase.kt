package com.troves.domain.usecase.shared

import com.troves.domain.utils.connectivity.ConnectivityObserver
import com.troves.domain.utils.connectivity.ConnectivityStatus
import kotlinx.coroutines.flow.Flow


class ObserveConnectivityUseCase(private val observer: ConnectivityObserver) {
    operator fun invoke(): Flow<ConnectivityStatus> = observer.status
    fun isOnlineNow(): Boolean = observer.currentStatus() == ConnectivityStatus.Available
}
