package com.troves.domain.utils


fun Throwable.toAppException(): Throwable {
    if (this is NoConnectionException) return this
    val name = this::class.simpleName ?: ""
    val msg = message ?: ""
    val looksOffline = name.contains("UnknownHost", ignoreCase = true) ||
        name.contains("ConnectException", ignoreCase = true) ||
        name.contains("SocketTimeout", ignoreCase = true) ||
        name.contains("NoRouteToHost", ignoreCase = true) ||
        name.contains("ApolloNetwork", ignoreCase = true) ||
        msg.contains("Unable to resolve host", ignoreCase = true) ||
        msg.contains("failed to connect", ignoreCase = true) ||
        msg.contains("Network is unreachable", ignoreCase = true)
    return if (looksOffline) NoConnectionException() else this
}