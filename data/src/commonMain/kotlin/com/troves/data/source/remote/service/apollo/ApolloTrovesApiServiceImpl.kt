package com.troves.data.source.remote.service.apollo

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.troves.data.source.remote.service.ktor.dto.Collection
import com.troves.data.source.remote.service.ktor.dto.CollectionImage
import com.troves.data.source.remote.service.ktor.dto.CustomCollectionResponse
import com.troves.data.source.remote.service.ktor.dto.MarketingEventsResponse
import com.troves.data.source.remote.service.ktor.dto.ProductDto
import com.troves.data.source.remote.service.ktor.dto.ProductResponse
import com.troves.data.source.remote.service.ktor.dto.SingleProductResponse
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.data.source.remote.service.apollo.graphql.GetProductsQuery
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.utils.Result




class ApolloTrovesApiServiceImpl(
private val apolloClient: ApolloClient
): TrovesApiService{
    override suspend fun createProduct(productDto: ProductDto): Result<ProductDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllProducts(
    ): Result<ProductResponse> {
        return try {
            val response = apolloClient.query(
                GetProductsQuery(
                    first = 20,
                    after = Optional.presentIfNotNull(null),
                    reverse = Optional.presentIfNotNull(null)
                )
            ).execute()

            if (response.hasErrors()) {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown GraphQL Error"
                return Result.Error(Exception(errorMessage))
            }

            val productsData = response.data?.products

            // Map the Relay edges/nodes structure to your Clean Architecture Domain Model
            val domainProducts = productsData?.edges?.map { edge ->
                edge.node.toDomainProduct()

            } ?: emptyList()

            val products: List<ProductDto> = domainProducts.map {
                ProductDto(
                    id = it.id,
                    title = it.title,
                    vendor = it.vendor,
                    status = it.status,
                    adminGraphqlApiId = null,
                    bodyHtml = null,
                    createdAt = null,
                    handle = null,
                    image = null,
                    images = emptyList(),
                    options = emptyList(),
                    productType = null,
                    publishedAt = null,
                    publishedScope = null,
                    tags = null,
                    updatedAt = null,
                    variants = emptyList(),
                )
            }
            val productResponse = ProductResponse(products = products)

            Result.Success(productResponse)

        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getProductsByQuery(queryMap: Map<String, String>): Result<ProductResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun searchProducts(params: ProductSearchParams): Result<List<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductImages(productId: String): Result<List<CollectionImage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(productId: String): Result<SingleProductResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun updateProduct(productId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productDto: ProductDto) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllBrands(): Result<Collection> {
        TODO("Not yet implemented")
    }

    override suspend fun getCategory(): Result<CustomCollectionResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllEventsById(eventId: String): MarketingEventsResponse {
        TODO("Not yet implemented")
    }

}
private fun GetProductsQuery.Node.toDomainProduct(): Product {
    return Product(
        id = this.id.toLongOrNull() ?: 0L,
        title = this.title,
        vendor = this.vendor,
        imageUrl = this.featuredImage?.url?.toString() ?: "",
        price = this.priceRange.maxVariantPrice.amount.toString(),
        status = "active"
    )
}
