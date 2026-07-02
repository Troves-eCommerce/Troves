package com.troves.data.source.remote.service.ktor.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomCollectionResponse(
    @SerialName("custom_collections")
    val customCollections: List<CustomCollectionDto>?
)

@Serializable
data class CustomCollectionDto(
    @SerialName("admin_graphql_api_id")
    val adminGraphqlApiId: String?,
    @SerialName("body_html")
    val bodyHtml: String?,
    @SerialName("handle")
    val handle: String?,
    @SerialName("id")
    val id: Long?,
    @SerialName("image")
    val customCollectionImage: CustomCollectionImage?,
    @SerialName("published_at")
    val publishedAt: String?,
    @SerialName("published_scope")
    val publishedScope: String?,
    @SerialName("sort_order")
    val sortOrder: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("updated_at")
    val updatedAt: String?
)

@Serializable
data class CustomCollectionImage(
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