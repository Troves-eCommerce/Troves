package com.troves.data.source.remote.service.apollo

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.data.source.remote.service.apollo.graphql.GetCollectionsQuery
import com.troves.data.source.remote.service.apollo.graphql.GetProductByIdQuery
import com.troves.data.source.remote.service.apollo.graphql.GetProductsBySearchQuery
import com.troves.data.source.remote.service.apollo.graphql.GetProductsQuery
import com.troves.data.source.remote.service.apollo.mapper.toCustomCollectionDto
import com.troves.data.source.remote.service.apollo.mapper.toDomainProduct
import com.troves.data.source.remote.service.apollo.mapper.toProductDto
import com.troves.data.source.remote.service.apollo.mapper.toSmartCollection
import com.troves.data.source.remote.service.apollo.util.runQuery
import com.troves.data.source.remote.service.apollo.util.toProductGid
import com.troves.data.source.remote.service.apollo.util.toQueryOptional
import com.troves.data.source.remote.service.apollo.util.toShopifySearchQuery
import com.troves.data.source.remote.service.ktor.dto.Collection
import com.troves.data.source.remote.service.ktor.dto.CollectionImage
import com.troves.data.source.remote.service.ktor.dto.CustomCollectionResponse
import com.troves.data.source.remote.service.ktor.dto.MarketingEventsResponse
import com.troves.data.source.remote.service.ktor.dto.ProductDto
import com.troves.data.source.remote.service.ktor.dto.ProductResponse
import com.troves.data.source.remote.service.ktor.dto.SingleProductResponse
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.utils.Result


class ApolloTrovesApiServiceImpl(
    private val apolloClient: ApolloClient
) : TrovesApiService {

    // region products
    override suspend fun createProduct(productDto: ProductDto): Result<ProductDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllProducts(): Result<ProductResponse> =
        apolloClient.runQuery(GetProductsQuery(first = DEFAULT_PAGE_SIZE)) { data ->
            ProductResponse(products = data.products.edges.map { it.node.productCard.toProductDto() })
        }

    override suspend fun getProductsByQuery(queryMap: Map<String, String>): Result<ProductResponse> =
        apolloClient.runQuery(
            GetProductsBySearchQuery(
                first = queryMap["limit"]?.toIntOrNull() ?: DEFAULT_PAGE_SIZE,
                query = queryMap.toShopifySearchQuery().toQueryOptional(),
            )
        ) { data ->
            ProductResponse(products = data.products.edges.map { it.node.productCard.toProductDto() })
        }

    override suspend fun searchProducts(params: ProductSearchParams): Result<List<Product>> =
        apolloClient.runQuery(
            GetProductsBySearchQuery(
                first = params.limit,
                query = params.toShopifySearchQuery().toQueryOptional(),
            )
        ) { data ->
            data.products.edges.map { it.node.productCard.toDomainProduct() }
        }

    override suspend fun getProductImages(productId: String): Result<List<CollectionImage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(productId: String): Result<SingleProductResponse> =
        apolloClient.runQuery(GetProductByIdQuery(id = productId.toProductGid())) { data ->
            val product = data.product?.productCard
                ?: throw NoSuchElementException("Product not found: $productId")
            SingleProductResponse(product = product.toProductDto())
        }

    override suspend fun updateProduct(productId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productDto: ProductDto) {
        TODO("Not yet implemented")
    }
    // endregion

    // region brands / categories
    override suspend fun getAllBrands(): Result<Collection> =
        apolloClient.runQuery(GetCollectionsQuery(first = DEFAULT_PAGE_SIZE, query = Optional.present(BRANDS_QUERY))) { data ->
            Collection(smartCollections = data.collections.edges.map { it.node.toSmartCollection() })
        }

    override suspend fun getCategory(): Result<CustomCollectionResponse> =
        apolloClient.runQuery(GetCollectionsQuery(first = DEFAULT_PAGE_SIZE, query = Optional.present(CATEGORIES_QUERY))) { data ->
            CustomCollectionResponse(customCollections = data.collections.edges.map { it.node.toCustomCollectionDto() })
        }
    // endregion

    // region events
    override suspend fun getAllEventsById(eventId: String): MarketingEventsResponse {
        TODO("Not yet implemented")
    }
    // endregion

    private companion object {
        const val DEFAULT_PAGE_SIZE = 250
        const val BRANDS_QUERY = "collection_type:Vendor"
        const val CATEGORIES_QUERY = "collection_type:Collection"
        const val PRODUCT_TYPE_QUERY =  "collection_type:product_type"
        
    }
}
