package com.troves.presintation.ui.checkout

import com.shopify.checkoutsheetkit.lifecycleevents.Address
import com.shopify.checkoutsheetkit.lifecycleevents.CartInfo
import com.shopify.checkoutsheetkit.lifecycleevents.CartLine
import com.shopify.checkoutsheetkit.lifecycleevents.CartLineImage
import com.shopify.checkoutsheetkit.lifecycleevents.CheckoutCompletedEvent
import com.shopify.checkoutsheetkit.lifecycleevents.DeliveryDetails
import com.shopify.checkoutsheetkit.lifecycleevents.DeliveryInfo
import com.shopify.checkoutsheetkit.lifecycleevents.Discount
import com.shopify.checkoutsheetkit.lifecycleevents.OrderDetails
import com.shopify.checkoutsheetkit.lifecycleevents.PaymentMethod
import com.shopify.checkoutsheetkit.lifecycleevents.Price
import com.shopify.checkoutsheetkit.pixelevents.MoneyV2
/**
 * Copyright (c) 2026 Wahid Ali Wahid Hussien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Author: Wahid Ali Wahid Hussien
 * Created: 03/07/2026
 */
fun CheckoutCompletedEvent.toDomain() =
    com.troves.domain.entity.CheckoutCompletedEvent(
        orderDetails = orderDetails.toDomain()
    )

fun OrderDetails.toDomain() =
    com.troves.domain.entity.OrderDetails(
        billingAddress = billingAddress?.toDomain(),
        cart = cart.toDomain(),
        deliveries = deliveries.map { it.toDomain() },
        email = email,
        id = id,
        paymentMethods = paymentMethods.map { it.toDomain() },
        phone = phone
    )
fun Address.toDomain() =
    com.troves.domain.entity.Address(
        address1,
        address2,
        city,
        countryCode,
        firstName,
        lastName,
        name,
        phone,
        postalCode,
        referenceId,
        zoneCode
    )

fun MoneyV2.toDomain() =
    com.troves.domain.entity.MoneyV2(
        amount = amount,
        currencyCode = currencyCode
    )

fun CartInfo.toDomain() =
    com.troves.domain.entity.CartInfo(
        lines = lines.map { it.toDomain() },
        price = price.toDomain(),
        token = token
    )


fun CartLine.toDomain() =
    com.troves.domain.entity.CartLine(
        discounts = discounts?.map { it.toDomain() },
        image = image?.toDomain(),
        merchandiseId = merchandiseId,
        price = price.toDomain(),
        productId = productId,
        quantity = quantity,
        title = title
    )


fun CartLineImage.toDomain() =
    com.troves.domain.entity.CartLineImage(
        altText = altText,
        lg = lg,
        md = md,
        sm = sm
    )


fun Discount.toDomain() =
    com.troves.domain.entity.Discount(
        amount = amount?.toDomain(),
        applicationType = applicationType,
        title = title,
        value = value,
        valueType = valueType
    )

fun DeliveryDetails.toDomain() =
    com.troves.domain.entity.DeliveryDetails(
        additionalInfo = additionalInfo,
        location = location?.toDomain(),
        name = name
    )


fun DeliveryInfo.toDomain() =
    com.troves.domain.entity.DeliveryInfo(
        details = details.toDomain(),
        method = method
    )

fun PaymentMethod.toDomain() =
    com.troves.domain.entity.PaymentMethod(
        details = details,
        type = type
    )

fun Price.toDomain() =
    com.troves.domain.entity.Price(
        discounts = discounts?.map { it.toDomain() },
        shipping = shipping?.toDomain(),
        subtotal = subtotal?.toDomain(),
        taxes = taxes?.toDomain(),
        total = total?.toDomain()
    )

