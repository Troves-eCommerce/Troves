package com.troves.data.source.remote.service.ktor

import com.troves.data.source.remote.service.ktor.dto.Collection
import com.troves.data.source.remote.service.ktor.dto.CollectionImage
import com.troves.data.source.remote.service.ktor.dto.CustomCollectionResponse
import com.troves.data.source.remote.service.ktor.dto.MarketingEventsResponse
import com.troves.data.source.remote.service.ktor.dto.ProductDto
import com.troves.data.source.remote.service.ktor.dto.ProductResponse
import com.troves.data.source.remote.service.ktor.dto.SingleProductResponse
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path
import com.troves.domain.utils.Result
import io.ktor.client.request.parameter

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
       return ktorClient.getResults {
            method = HttpMethod.Get
            params.vendor?.let { parameter("vendor", it) }
            params.productType?.let { parameter("product_type", it) }
            params.collectionId?.let { parameter("collection_id", it) }
            params.status?.let { parameter("status", it.restValue) }
            parameter("limit", params.limit)
        }
    }

    override suspend fun getProductImages(productId: String): Result<List<CollectionImage>> {
        return ktorClient.getResults {
            method = HttpMethod.Get
            url {
                path("products/$productId/images.json")
            }
        }
    }

    override suspend fun getProductById(productId: String): Result<SingleProductResponse> {
        return ktorClient.getResults {
            method = HttpMethod.Get
            url {
                path("products/$productId.json")
            }
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


}