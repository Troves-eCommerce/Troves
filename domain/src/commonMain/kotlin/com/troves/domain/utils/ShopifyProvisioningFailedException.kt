package com.troves.domain.utils


class ShopifyProvisioningFailedException(
    message: String = "We couldn't connect your account to the store. Please try again.",
) : Exception(message)
