package com.troves.data.source.remote.service.apollo.mapper

import com.troves.data.source.remote.service.apollo.graphql.admin.GetCollectionsQuery
import com.troves.data.source.remote.service.apollo.util.gidToLong
import com.troves.data.source.remote.service.ktor.dto.CustomCollectionDto
import com.troves.data.source.remote.service.ktor.dto.SmartCollection


// Brands (vendor collections) keep their original title: it must stay equal to `Product.vendor`
// (which is not localized) so the vendor facet filter in TrovesRepositoryImpl.searchProducts still
// matches. Brand names are proper nouns and generally aren't translated anyway.
internal fun GetCollectionsQuery.Node.toSmartCollection(): SmartCollection = SmartCollection(
    adminGraphqlApiId = null,
    bodyHtml = null,
    disjunctive = null,
    handle = null,
    id = id.gidToLong(),
    collectionImage = collectionImage(image?.url?.toString()),
    publishedAt = null,
    publishedScope = null,
    rules = null,
    sortOrder = null,
    title = title,
    updatedAt = null,
)

internal fun GetCollectionsQuery.Node.toCustomCollectionDto(): CustomCollectionDto = CustomCollectionDto(
    adminGraphqlApiId = null,
    bodyHtml = null,
    handle = null,
    id = id.gidToLong(),
    customCollectionImage = customCollectionImage(image?.url?.toString()),
    publishedAt = null,
    publishedScope = null,
    sortOrder = null,
    title = localizedTitle(),
    updatedAt = null,
)

/** Registered locale title (e.g. Arabic) for the collection, falling back to the base English title. */
private fun GetCollectionsQuery.Node.localizedTitle(): String =
    translations.firstOrNull { it.key == TRANSLATION_KEY_TITLE }?.value?.takeIf { it.isNotBlank() }
        ?: title
