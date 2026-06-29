package com.troves.data.repository

import com.troves.domain.AuthenticationRepository

interface PlatformAuthenticationRepository : AuthenticationRepository

expect fun createAuthenticationRepository(): PlatformAuthenticationRepository
