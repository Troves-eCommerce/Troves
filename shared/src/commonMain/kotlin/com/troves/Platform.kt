package com.troves

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform