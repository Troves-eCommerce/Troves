package com.troves.domain.utils

class RateLimitException(
    val retryAfterSeconds: Int,
) : Exception("Rate limited. Retry after ${retryAfterSeconds}s")
