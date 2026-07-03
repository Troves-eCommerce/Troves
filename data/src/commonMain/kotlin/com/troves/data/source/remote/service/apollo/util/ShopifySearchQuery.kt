package com.troves.data.source.remote.service.apollo.util

import com.troves.domain.entity.ProductSearchParams



private val PRODUCT_FILTER_KEYS = setOf("vendor", "product_type", "tag", "title", "available_for_sale")

internal fun Map<String, String>.toShopifySearchQuery(): String =
    entries
        .filter { it.key in PRODUCT_FILTER_KEYS && it.value.isNotBlank() }
        .joinToString(" ") { "${it.key}:${it.value}" }

internal fun ProductSearchParams.toShopifySearchQuery(): String = buildList {
    vendor?.takeIf { it.isNotBlank() }?.let { add("vendor:$it") }
    productType?.takeIf { it.isNotBlank() }?.let { add("product_type:$it") }
    query?.takeIf { it.isNotBlank() }?.let { add(it) }
}.joinToString(" ")
