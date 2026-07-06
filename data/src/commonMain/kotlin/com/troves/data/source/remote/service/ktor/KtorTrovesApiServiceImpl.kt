package com.troves.data.source.remote.service.ktor

import com.troves.data.mapper.toDomain
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.data.source.remote.service.ktor.dto.Collection
import com.troves.data.source.remote.service.ktor.dto.CollectionImage
import com.troves.data.source.remote.service.ktor.dto.CustomCollectionResponse
import com.troves.data.source.remote.service.ktor.dto.MarketingEventsResponse
import com.troves.data.source.remote.service.ktor.dto.ProductDto
import com.troves.data.source.remote.service.ktor.dto.ProductResponse
import com.troves.data.source.remote.service.ktor.dto.SingleProductResponse
import com.troves.domain.entity.DiscountCode
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.utils.Result
import com.troves.domain.utils.map
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod
import io.ktor.http.path

class KtorTrovesApiServiceImpl(
    private val ktorClient: HttpClient
) : TrovesApiService {
    override suspend fun createProduct(productDto: ProductDto): Result<ProductDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllProducts(): Result<ProductResponse> =
        ktorClient.getResults {
            method = HttpMethod.Get
            url { path("products.json") }
        }

    override suspend fun getProductsByQuery(queryMap: Map<String, String>): Result<ProductResponse> =
        ktorClient.getResults {
            method = HttpMethod.Get
            url {
                path("products.json")
                queryMap.entries.forEach {
                    parameter(it.key, it.value)
                }
            }
        }

    override suspend fun searchProducts(params: ProductSearchParams): Result<List<Product>> {
       val response: Result<ProductResponse> = ktorClient.getResults {
            method = HttpMethod.Get
            url { path("products.json") }
            val vendorToUse = params.vendor ?: params.vendors?.firstOrNull()
            val productTypeToUse = params.productType ?: params.productTypes?.firstOrNull()
            vendorToUse?.let { parameter("vendor", it) }
            productTypeToUse?.let { parameter("product_type", it) }
            params.collectionId?.let { parameter("collection_id", it) }
            params.status?.let { parameter("status", it.restValue) }
            parameter("limit", 250)
        }
        return response.map { res -> res.products?.map { it.toDomain() }.orEmpty() }
    }

    override suspend fun getProductsByVendor(vendorName: String): Result<List<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductsByCollection(collectionId: String): Result<List<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductImages(productId: String): Result<List<CollectionImage>> {
        return ktorClient.getResults {
            method = HttpMethod.Get
            url {
                path("products/$productId/images.json")
            }
        }
    }

    override suspend fun getProductById(productId: String): Result<Product> {
        val response: Result<SingleProductResponse> = ktorClient.getResults {
            method = HttpMethod.Get
            url {
                path("products/$productId.json")
            }
        }
        return response.map { single ->
            single.product?.toDomain()
                ?: throw NoSuchElementException("Product not found: $productId")
        }
    }

    override suspend fun updateProduct(productId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productDto: ProductDto) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllBrands(): Result<Collection> =
        ktorClient.getResults {
            method = HttpMethod.Get
            url { path("smart_collections.json") }
        }

    override suspend fun getCategory(): Result<CustomCollectionResponse> =
        ktorClient.getResults {
            method = HttpMethod.Get
            url { path("custom_collections.json") }
        }


    override suspend fun getAllEventsById(eventId: String): MarketingEventsResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getDiscountCodes(): Result<List<DiscountCode>> {
        TODO("Not yet implemented")
    }

    override suspend fun createOrder(
        email: String?,
        address: com.troves.domain.entity.Address,
        lineItems: List<Pair<String, Int>>,
    ): Result<String> =
        Result.Error(UnsupportedOperationException("Order creation is only available via the Admin GraphQL API"))
}