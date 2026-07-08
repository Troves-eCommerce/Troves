package com.troves.domain.utils.connectivity

import kotlinx.coroutines.flow.Flow

enum class ConnectivityStatus { Available, Unavailable }

interface ConnectivityObserver {
    val status: Flow<ConnectivityStatus>

    fun currentStatus(): ConnectivityStatus
}
