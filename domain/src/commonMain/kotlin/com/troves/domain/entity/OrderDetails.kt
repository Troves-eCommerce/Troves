package com.troves.domain.entity


data class CheckoutCompletedEvent(
     val orderDetails: OrderDetails
)


data class Address(
    val address1: String? = null,
    val address2: String? = null,
    val city: String? = null,
    val countryCode: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val postalCode: String? = null,
    val referenceId: String? = null,
    val zoneCode: String? = null,
)

data class MoneyV2(

    val amount: Double? = null,
    val currencyCode: String? = null
)

data class CartInfo(
    val lines: List<CartLine>,
    val price: Price,
    val token: String,
)

data class CartLineImage(
    val altText: String? = null,
    val lg: String,
    val md: String,
    val sm: String,
)

data class CartLine(
    val discounts: List<Discount>? = emptyList(),
    val image: CartLineImage? = null,
    val merchandiseId: String? = null,
    val price: MoneyV2,
    val productId: String? = null,
    val quantity: Int,
    val title: String,
)

data class DeliveryDetails(
    val additionalInfo: String? = null,
    val location: Address? = null,
    val name: String? = null,
)

data class DeliveryInfo(
    val details: DeliveryDetails,
    val method: String,
)

data class Discount(
    val amount: MoneyV2? = null,
    val applicationType: String? = null,
    val title: String? = null,
    val value: Double? = null,
    val valueType: String? = null,
)

data class OrderDetails(
    val billingAddress: Address? = null,
    val cart: CartInfo,
    val deliveries: List<DeliveryInfo> = emptyList(),
    val email: String? = null,
    val id: String,
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val phone: String? = null,
)

data class PaymentMethod(
    val details: Map<String, String>? = emptyMap(),
    val type: String,
)

data class Price(
    val discounts: List<Discount>? = emptyList(),
    val shipping: MoneyV2? = null,
    val subtotal: MoneyV2? = null,
    val taxes: MoneyV2? = null,
    val total: MoneyV2? = null,
)