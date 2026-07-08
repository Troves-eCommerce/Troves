package com.troves.data.platform.connectivity

import com.troves.domain.utils.connectivity.ConnectivityObserver
import com.troves.domain.utils.connectivity.ConnectivityStatus
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_queue_create

@OptIn(ExperimentalForeignApi::class)
actual class ConnectivityObserverFactory {
    actual fun create(): ConnectivityObserver = IosConnectivityObserver()
}

@OptIn(ExperimentalForeignApi::class)
private class IosConnectivityObserver : ConnectivityObserver {

    private var last: ConnectivityStatus = ConnectivityStatus.Available

    override val status: Flow<ConnectivityStatus> = callbackFlow {
        val monitor = nw_path_monitor_create()
        val queue = dispatch_queue_create("com.troves.connectivity.monitor", null)

        nw_path_monitor_set_update_handler(monitor) { path ->
            val s = if (nw_path_get_status(path) == nw_path_status_satisfied) {
                ConnectivityStatus.Available
            } else {
                ConnectivityStatus.Unavailable
            }
            last = s
            trySend(s)
        }
        nw_path_monitor_set_queue(monitor, queue)
        nw_path_monitor_start(monitor)

        awaitClose { nw_path_monitor_cancel(monitor) }
    }.distinctUntilChanged()

    override fun currentStatus(): ConnectivityStatus = last
}
