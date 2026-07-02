package com.troves.data.source.remote.service.apollo.mapper

import com.troves.data.source.remote.service.ktor.dto.CollectionImage
import com.troves.data.source.remote.service.ktor.dto.CustomCollectionImage
import com.troves.data.source.remote.service.ktor.dto.Variant


internal fun collectionImage(src: String?): CollectionImage =
    CollectionImage(alt = null, createdAt = null, height = null, src = src, width = null)

internal fun customCollectionImage(src: String?): CustomCollectionImage =
    CustomCollectionImage(alt = null, createdAt = null, height = null, src = src, width = null)

internal fun priceVariant(price: String): Variant = Variant(
    adminGraphqlApiId = null,
    compareAtPrice = null,
    createdAt = null,
    fulfillmentService = null,
    grams = null,
    id = null,
    imageId = null,
    inventoryItemId = null,
    inventoryManagement = null,
    inventoryPolicy = null,
    inventoryQuantity = null,
    oldInventoryQuantity = null,
    option1 = null,
    option2 = null,
    position = null,
    price = price,
    productId = null,
    requiresShipping = null,
    sku = null,
    taxable = null,
    title = null,
    updatedAt = null,
    weight = null,
    weightUnit = null,
)
