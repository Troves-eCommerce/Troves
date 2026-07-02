package com.troves.data.source.remote.service.apollo.mapper

import com.troves.data.source.remote.service.apollo.graphql.admin.GetCollectionsQuery
import com.troves.data.source.remote.service.apollo.util.gidToLong
import com.troves.data.source.remote.service.ktor.dto.CustomCollectionDto
import com.troves.data.source.remote.service.ktor.dto.SmartCollection


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
    title = title,
    updatedAt = null,
)
