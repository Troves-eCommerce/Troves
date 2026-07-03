package com.troves.domain.entity

data class Address(
    val id: String,
    val label: String,
    val icon: AddressIcon,
    val phone: String,
    val lines: List<String>,
    val isDefault: Boolean = false,
    val note: String? = null
)

enum class AddressIcon { HOME, WORK }
