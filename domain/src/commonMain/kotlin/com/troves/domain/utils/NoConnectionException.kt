package com.troves.domain.utils


class NoConnectionException(
    message: String = "No internet connection"
) : Exception(message)