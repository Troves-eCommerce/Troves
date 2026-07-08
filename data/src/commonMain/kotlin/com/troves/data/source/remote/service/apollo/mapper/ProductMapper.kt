package com.troves.data.source.remote.service.apollo.mapper

import com.troves.data.source.remote.service.apollo.graphql.admin.GetProductsByCollectionQuery
import com.troves.data.source.remote.service.apollo.graphql.admin.fragment.ProductCard
import com.troves.data.source.remote.service.apollo.util.gidToLong
import com.troves.data.source.remote.service.ktor.dto.Option
import com.troves.data.source.remote.service.ktor.dto.ProductDto
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductOption
import com.troves.domain.entity.ProductVariant

internal fun ProductCard.toProductDto(): ProductDto = ProductDto(
    adminGraphqlApiId = null,
    bodyHtml = localized(TRANSLATION_KEY_DESCRIPTION) ?: descriptionHtml.toString(),
    createdAt = null,
    handle = null,
    id = id.gidToLong(),
    image = collectionImage(featuredImage?.url?.toString()),
    images = images.edges.map { collectionImage(it.node.url.toString()) },
    options = options.map { option ->
        Option(
            id = null,
            name = option.name,
            position = null,
            productId = null,
            values = option.values,
        )
    },
    productType = null,
    publishedAt = null,
    publishedScope = null,
    status = status.rawValue.lowercase(),
    tags = null,
    title = localized(TRANSLATION_KEY_TITLE) ?: title,
    updatedAt = null,
    variants = listOf(priceVariant(priceRangeV2.minVariantPrice.amount.toString())),
    vendor = vendor,
)

internal fun ProductCard.toDomainProduct(): Product = Product(
    id = id.gidToLong() ?: 0L,
    title = localized(TRANSLATION_KEY_TITLE) ?: title,
    vendor = vendor,
    price = priceRangeV2.minVariantPrice.amount.toString(),
    imageUrl = featuredImage?.url?.toString() ?: "",
    status = status.rawValue.lowercase(),
    images = images.edges.map { it.node.url.toString() },
    sizes = optionValuesFor("Size"),
    colors = optionValuesFor("Color"),
    description = localized(TRANSLATION_KEY_DESCRIPTION) ?: descriptionHtml.toString(),
    options = options.map { ProductOption(name = it.name, values = it.values) },
    variants = variants.nodes.map { node ->
        ProductVariant(
            variantId = node.id,
            title = node.title,
            price = node.price.toString(),
            available = node.availableForSale,
            inventoryQuantity = node.inventoryQuantity,
            selectedOptions = node.selectedOptions.associate { it.name to it.value },
        )
    },
)

private fun ProductCard.optionValuesFor(optionName: String): List<String> =
    options.firstOrNull { it.name.equals(optionName, ignoreCase = true) }
        ?.values
        .orEmpty()

internal fun GetProductsByCollectionQuery.Node.toDomainProduct(): Product = Product(
    id = id.gidToLong() ?: 0L,
    title = translations.firstOrNull { it.key == TRANSLATION_KEY_TITLE }?.value?.takeIf { it.isNotBlank() }
        ?: title,
    vendor = "",
    price = priceRangeV2.minVariantPrice.amount.toString(),
    imageUrl = featuredImage?.url?.toString() ?: "",
    status = "",
)

/**
 * Registered locale value for [key] (e.g. "title", "body_html"), or null when the store has no
 * translation for that key in the requested locale — so the mapper falls back to the base English
 * field. The locale is chosen by the query (`translations(locale:)`).
 */
private fun ProductCard.localized(key: String): String? =
    translations.firstOrNull { it.key == key }?.value?.takeIf { it.isNotBlank() }

// Shopify translatable content keys (Admin API `translations`).
internal const val TRANSLATION_KEY_TITLE = "title"
internal const val TRANSLATION_KEY_DESCRIPTION = "body_html"
