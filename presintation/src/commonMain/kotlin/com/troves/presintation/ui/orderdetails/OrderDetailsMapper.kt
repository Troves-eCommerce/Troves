package com.troves.presintation.ui.orderdetails

import com.troves.domain.entity.CartMoney
import com.troves.domain.entity.Order
import com.troves.presintation.ui.orderdetails.components.TimelineStepData
import org.jetbrains.compose.resources.getString
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.order_details_shipping_free
import troves.presintation.generated.resources.order_details_timeline_cancelled_desc
import troves.presintation.generated.resources.order_details_timeline_cancelled_title
import troves.presintation.generated.resources.order_details_timeline_delivered_desc
import troves.presintation.generated.resources.order_details_timeline_delivered_pending
import troves.presintation.generated.resources.order_details_timeline_delivered_title
import troves.presintation.generated.resources.order_details_timeline_placed_desc
import troves.presintation.generated.resources.order_details_timeline_placed_title
import troves.presintation.generated.resources.order_details_timeline_processing_desc
import troves.presintation.generated.resources.order_details_timeline_shipped_desc
import troves.presintation.generated.resources.order_details_timeline_shipped_title
import troves.presintation.generated.resources.order_status_processing

suspend fun Order.toDetailsUi(): OrderDetailsUi {
    val statusStr = listOfNotNull(financialStatus, fulfillmentStatus)
        .filter { it.isNotBlank() }
        .joinToString(" · ") { it.lowercase().replaceFirstChar(Char::uppercase) }
        .ifBlank { getString(Res.string.order_status_processing) }

    val isDelivered = fulfillmentStatus?.lowercase() == "fulfilled"
    val isShipped = fulfillmentStatus?.lowercase() == "shipped" || isDelivered
    val isCancelled = financialStatus?.lowercase() == "refunded" || financialStatus?.lowercase() == "voided"

    val timeline = buildList {
        add(
            TimelineStepData(
                title = getString(Res.string.order_details_timeline_placed_title),
                date = processedAt.take(10),
                description = getString(Res.string.order_details_timeline_placed_desc),
                isCompleted = true,
                isCurrent = !isShipped && !isDelivered && !isCancelled
            )
        )
        if (!isCancelled) {
            add(
                TimelineStepData(
                    title = getString(Res.string.order_status_processing),
                    description = getString(Res.string.order_details_timeline_processing_desc),
                    isCompleted = isShipped || isDelivered,
                    isCurrent = !isShipped && !isDelivered
                )
            )
            add(
                TimelineStepData(
                    title = getString(Res.string.order_details_timeline_shipped_title),
                    description = getString(Res.string.order_details_timeline_shipped_desc),
                    isCompleted = isDelivered,
                    isCurrent = isShipped && !isDelivered
                )
            )
            add(
                TimelineStepData(
                    title = getString(Res.string.order_details_timeline_delivered_title),
                    description = if (isDelivered) getString(Res.string.order_details_timeline_delivered_desc) else getString(Res.string.order_details_timeline_delivered_pending),
                    isCompleted = isDelivered,
                    isCurrent = isDelivered
                )
            )
        } else {
            add(
                TimelineStepData(
                    title = getString(Res.string.order_details_timeline_cancelled_title),
                    description = getString(Res.string.order_details_timeline_cancelled_desc),
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
        shipping = shipping?.let { format(it) } ?: getString(Res.string.order_details_shipping_free),
        tax = tax?.let { format(it) } ?: "-",
        total = format(total)
    )
}

private fun format(money: CartMoney): String =
    if (money.currencyCode == "USD") "$${money.amount}" else "${money.amount} ${money.currencyCode}"
