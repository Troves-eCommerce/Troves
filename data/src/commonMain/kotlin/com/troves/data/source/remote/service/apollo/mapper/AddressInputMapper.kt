package com.troves.data.source.remote.service.apollo.mapper

import com.apollographql.apollo.api.Optional
import com.troves.data.source.remote.service.apollo.graphql.storefront.type.CartAddressInput
import com.troves.data.source.remote.service.apollo.graphql.storefront.type.CartDeliveryAddressInput
import com.troves.data.source.remote.service.apollo.graphql.storefront.type.CartSelectableAddressInput
import com.troves.data.source.remote.service.apollo.graphql.storefront.type.CountryCode
import com.troves.data.source.remote.service.apollo.graphql.storefront.type.MailingAddressInput
import com.troves.domain.entity.Address

internal fun Address.toStorefrontMailingAddressInput(): MailingAddressInput = MailingAddressInput(
    address1 = Optional.presentIfNotNull(address1),
    address2 = Optional.presentIfNotNull(address2),
    city = Optional.presentIfNotNull(city),
    company = Optional.presentIfNotNull(company),
    country = Optional.presentIfNotNull(country),
    firstName = Optional.presentIfNotNull(firstName),
    lastName = Optional.presentIfNotNull(lastName),
    phone = Optional.presentIfNotNull(phone),
    province = Optional.presentIfNotNull(province),
    zip = Optional.presentIfNotNull(zip),
)

internal fun Address.toCartSelectableAddressInput(): CartSelectableAddressInput {
    val countryCodeEnum = countryCode
        ?.let { CountryCode.safeValueOf(it) }
        ?.takeIf { it != CountryCode.UNKNOWN__ }

    val delivery = CartDeliveryAddressInput(
        address1 = Optional.presentIfNotNull(address1),
        address2 = Optional.presentIfNotNull(address2),
        city = Optional.presentIfNotNull(city),
        company = Optional.presentIfNotNull(company),
        countryCode = Optional.presentIfNotNull(countryCodeEnum),
        firstName = Optional.presentIfNotNull(firstName),
        lastName = Optional.presentIfNotNull(lastName),
        phone = Optional.presentIfNotNull(phone),
        provinceCode = Optional.presentIfNotNull(provinceCode),
        zip = Optional.presentIfNotNull(zip),
    )

    return CartSelectableAddressInput(
        address = CartAddressInput(deliveryAddress = Optional.present(delivery)),
        selected = Optional.present(true),
    )
}
