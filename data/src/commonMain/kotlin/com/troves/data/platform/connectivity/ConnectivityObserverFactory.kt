package com.troves.data.platform.connectivity

import com.troves.domain.utils.connectivity.ConnectivityObserver

expect class ConnectivityObserverFactory {
    fun create(): ConnectivityObserver
}
