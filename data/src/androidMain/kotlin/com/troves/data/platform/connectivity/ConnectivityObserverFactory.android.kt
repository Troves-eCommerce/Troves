package com.troves.data.platform.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.troves.domain.utils.connectivity.ConnectivityObserver
import com.troves.domain.utils.connectivity.ConnectivityStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

actual class ConnectivityObserverFactory(private val context: Context) {
    actual fun create(): ConnectivityObserver = AndroidConnectivityObserver(context)
}

private class AndroidConnectivityObserver(context: Context) : ConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    private fun NetworkCapabilities?.isUsable(): Boolean =
        this != null &&
            hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

    private fun isOnline(): Boolean =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork).isUsable()

    override val status: Flow<ConnectivityStatus> = callbackFlow {
        // Emit current state up-front so subscribers never start "unknown".
        trySend(if (isOnline()) ConnectivityStatus.Available else ConnectivityStatus.Unavailable)

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(if (caps.isUsable()) ConnectivityStatus.Available else ConnectivityStatus.Unavailable)
            }

            override fun onLost(network: Network) {
                trySend(ConnectivityStatus.Unavailable)
            }

            override fun onUnavailable() {
                trySend(ConnectivityStatus.Unavailable)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    override fun currentStatus(): ConnectivityStatus =
        if (isOnline()) ConnectivityStatus.Available else ConnectivityStatus.Unavailable
}
