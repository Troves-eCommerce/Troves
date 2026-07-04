package com.troves.domain.utils


class ShopifyAuthRequiredException(
    message: String = "Please sign in again to manage your addresses",
) : Exception(message)
