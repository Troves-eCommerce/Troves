package com.troves.domain.entity


data class Address(
    val id: String,
    val address1: String?,
    val address2: String?,
    val city: String?,
    val province: String?,
    val provinceCode: String?,
    val country: String?,
    val countryCode: String?,
    val zip: String?,
    val phone: String?,
    val firstName: String?,
    val lastName: String?,
    val company: String?,
) {
    val isDeliverable: Boolean
        get() = !address1.isNullOrBlank() && !city.isNullOrBlank()

    val recipientName: String
        get() = listOfNotNull(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")

    val singleLine: String
        get() = listOfNotNull(address1, address2, city, province, country, zip)
            .filter { it.isNotBlank() }
            .joinToString(", ")
}
