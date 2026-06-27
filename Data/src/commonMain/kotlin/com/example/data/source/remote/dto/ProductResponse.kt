package com.example.data.source.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    @SerialName("products")
    val products: List<ProductDto>?
)


@Serializable
data class ProductDto(
    @SerialName("admin_graphql_api_id")
    val adminGraphqlApiId: String?,
    @SerialName("body_html")
    val bodyHtml: String?,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("handle")
    val handle: String?,
    @SerialName("id")
    val id: Long?,
    @SerialName("image")
    val image: CollectionImage?,
    @SerialName("images")
    val images: List<CollectionImage?>?,
    @SerialName("options")
    val options: List<Option?>?,
    @SerialName("product_type")
    val productType: String?,
    @SerialName("published_at")
    val publishedAt: String?,
    @SerialName("published_scope")
    val publishedScope: String?,
    @SerialName("status")
    val status: String?,
    @SerialName("tags")
    val tags: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("updated_at")
    val updatedAt: String?,
    @SerialName("variants")
    val variants: List<Variant?>?,
    @SerialName("vendor")
    val vendor: String?
)


@Serializable
data class Variant(
    @SerialName("admin_graphql_api_id")
    val adminGraphqlApiId: String?,
    @SerialName("compare_at_price")
    val compareAtPrice: String?,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("fulfillment_service")
    val fulfillmentService: String?,
    @SerialName("grams")
    val grams: Int?,
    @SerialName("id")
    val id: Long?,
    @SerialName("image_id")
    val imageId: String?,
    @SerialName("inventory_item_id")
    val inventoryItemId: Long?,
    @SerialName("inventory_management")
    val inventoryManagement: String?,
    @SerialName("inventory_policy")
    val inventoryPolicy: String?,
    @SerialName("inventory_quantity")
    val inventoryQuantity: Int?,
    @SerialName("old_inventory_quantity")
    val oldInventoryQuantity: Int?,
    @SerialName("option1")
    val option1: String?,
    @SerialName("option2")
    val option2: String?,
    @SerialName("position")
    val position: Int?,
    @SerialName("price")
    val price: String?,
    @SerialName("product_id")
    val productId: Long?,
    @SerialName("requires_shipping")
    val requiresShipping: Boolean?,
    @SerialName("sku")
    val sku: String?,
    @SerialName("taxable")
    val taxable: Boolean?,
    @SerialName("title")
    val title: String?,
    @SerialName("updated_at")
    val updatedAt: String?,
    @SerialName("weight")
    val weight: Double?,
    @SerialName("weight_unit")
    val weightUnit: String?
)


@Serializable
data class Option(
    @SerialName("id")
    val id: Long?,
    @SerialName("name")
    val name: String?,
    @SerialName("position")
    val position: Int?,
    @SerialName("product_id")
    val productId: Long?,
    @SerialName("values")
    val values: List<String?>?
)

@Serializable
data class Image(
    @SerialName("admin_graphql_api_id")
    val adminGraphqlApiId: String?,
    @SerialName("alt")
    val alt: String?,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("height")
    val height: Int?,
    @SerialName("id")
    val id: Long?,
    @SerialName("position")
    val position: Int?,
    @SerialName("product_id")
    val productId: Long?,
    @SerialName("src")
    val src: String?,
    @SerialName("updated_at")
    val updatedAt: String?,
    @SerialName("width")
    val width: Int?
)



@Serializable
data class SmartCollection(
    @SerialName("admin_graphql_api_id")
    val adminGraphqlApiId: String?,
    @SerialName("body_html")
    val bodyHtml: String?,
    @SerialName("disjunctive")
    val disjunctive: Boolean?,
    @SerialName("handle")
    val handle: String?,
    @SerialName("id")
    val id: Long?,
    @SerialName("image")
    val collectionImage: CollectionImage?,
    @SerialName("published_at")
    val publishedAt: String?,
    @SerialName("published_scope")
    val publishedScope: String?,
    @SerialName("rules")
    val rules: List<Rule?>?,
    @SerialName("sort_order")
    val sortOrder: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("updated_at")
    val updatedAt: String?
)