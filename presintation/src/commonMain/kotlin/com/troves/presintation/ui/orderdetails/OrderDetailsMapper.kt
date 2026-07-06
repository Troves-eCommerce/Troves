package com.troves.presintation.ui.orderdetails

import com.troves.domain.entity.CartMoney
import com.troves.domain.entity.Order
import com.troves.presintation.ui.orderdetails.components.TimelineStepData

fun Order.toDetailsUi(): OrderDetailsUi {
    val statusStr = listOfNotNull(financialStatus, fulfillmentStatus)
        .filter { it.isNotBlank() }
        .joinToString(" · ") { it.lowercase().replaceFirstChar(Char::uppercase) }
        .ifBlank { "Processing" }

    val isDelivered = fulfillmentStatus?.lowercase() == "fulfilled"
    val isShipped = fulfillmentStatus?.lowercase() == "shipped" || isDelivered
    val isCancelled = financialStatus?.lowercase() == "refunded" || financialStatus?.lowercase() == "voided"

    val timeline = buildList {
        add(
            TimelineStepData(
                title = "Order Placed",
                date = processedAt.take(10),
                description = "Your order has been placed successfully.",
                isCompleted = true,
                isCurrent = !isShipped && !isDelivered && !isCancelled
            )
        )
        if (!isCancelled) {
            add(
                TimelineStepData(
                    title = "Processing",
                    description = "We are preparing your order.",
                    isCompleted = isShipped || isDelivered,
                    isCurrent = !isShipped && !isDelivered
                )
            )
            add(
                TimelineStepData(
                    title = "Shipped",
                    description = "Your order is on the way.",
                    isCompleted = isDelivered,
                    isCurrent = isShipped && !isDelivered
                )
            )
            add(
                TimelineStepData(
                    title = "Delivered",
                    description = if (isDelivered) "Your order has been delivered." else "Expected delivery soon",
                    isCompleted = isDelivered,
                    isCurrent = isDelivered
                )
            )
        } else {
            add(
                TimelineStepData(
                    title = "Cancelled",
                    description = "Your order has been cancelled.",
                    isCompleted = true,
                    isCurrent = true
                )
            )
        }
    }

    return OrderDetailsUi(
        id = id,
        orderNumber = name,
        orderDate = processedAt.take(10),
        status = statusStr,
        totalAmount = format(total),
        itemCount = lineItems.sumOf { it.quantity },
        timelineSteps = timeline,
        items = lineItems.map { item ->
            OrderItemUi(
                id = item.title + item.variantTitle, // Generate a unique key
                name = item.title,
                variant = item.variantTitle ?: "",
                quantity = item.quantity,
                price = format(item.price),
                imageUrl = item.imageUrl
            )
        },
        subtotal = format(subtotal),
        shipping = shipping?.let { format(it) } ?: "Free",
        tax = tax?.let { format(it) } ?: "-",
        total = format(total)
    )
}

private fun format(money: CartMoney): String =
    if (money.currencyCode == "USD") "$${money.amount}" else "${money.amount} ${money.currencyCode}"
