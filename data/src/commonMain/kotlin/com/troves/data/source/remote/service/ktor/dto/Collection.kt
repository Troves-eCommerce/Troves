package com.troves.data.source.remote.service.ktor.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Collection(
    @SerialName("smart_collections")
    val smartCollections: List<SmartCollection?>?
)



@Serializable
data class Rule(
    @SerialName("column")
    val column: String?,
    @SerialName("condition")
    val condition: String?,
    @SerialName("relation")
    val relation: String?
)



@Serializable
data class CollectionImage(
    @SerialName("alt")
    val alt: String?,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("height")
    val height: Int?,
    @SerialName("src")
    val src: String?,
    @SerialName("width")
    val width: Int?
)


