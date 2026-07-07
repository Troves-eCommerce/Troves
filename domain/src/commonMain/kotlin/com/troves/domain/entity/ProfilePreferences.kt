package com.troves.domain.entity

data class ProfilePreferences(
    val isLoggedIn: Boolean,
    val language: String,
    val themeMode: String,
    val currency: String,
    val displayName: String? = null,
    val email: String? = null,
    val profileImageUrl: String? = null
)
