package com.troves.domain.entity

data class UserProfile(
    val id: String?,
    val email: String?,
    val isEmailVerified: Boolean = false
)