package com.troves.data.source.remote.service

import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.dto.Collection
import com.troves.data.source.remote.dto.CollectionImage
import com.troves.data.source.remote.dto.CustomCollectionResponse
import com.troves.data.source.remote.dto.MarketingEventsResponse
import com.troves.data.source.remote.dto.ProductDto
import com.troves.data.source.remote.dto.ProductResponse
import com.troves.domain.Result
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

class TrovesApiServiceImpl(
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

    override suspend fun getProductImages(productId: String): Result<List<CollectionImage>> {
        return ktorClient.getResults {
            method = HttpMethod.Get
            url{
                path("/products/$productId/images.json")
            }
        }
    }

    override suspend fun getProductById(productId: String): Result<ProductResponse> {
        return ktorClient.getResults {
            method = HttpMethod.Get
            url{
                path("/products/$productId/.json")
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