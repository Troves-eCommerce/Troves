package com.example.data.source.remote.service

import com.example.data.source.remote.RemoteDatasource
import com.example.data.source.remote.dto.Collection
import com.example.data.source.remote.dto.CollectionImage
import com.example.data.source.remote.dto.MarketingEventsResponse
import com.example.data.source.remote.dto.ProductDto
import com.example.data.source.remote.dto.ProductResponse
import com.example.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.HttpMethod
import io.ktor.http.path

class TrovesApiService(
    private val ktorClient: HttpClient
) : RemoteDatasource {
    override suspend fun createProduct(productDto: ProductDto): Result<ProductDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllProducts(): Result<ProductResponse> =
        ktorClient.getResults {
            method = HttpMethod.Get
            url { path("products.json") }
        }

    override suspend fun getProductImages(productId: String): Result<List<CollectionImage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(productId: String): Result<ProductResponse> {
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

    override suspend fun getCategory(): Result<Collection> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllEventsById(eventId: String): MarketingEventsResponse {
        TODO("Not yet implemented")
    }


}