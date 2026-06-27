package com.example.data.source.remote

import com.example.data.source.remote.dto.Collection
import com.example.data.source.remote.dto.CollectionImage
import com.example.data.source.remote.dto.MarketingEventsResponse
import com.example.data.source.remote.dto.ProductResponse
import com.example.data.source.remote.dto.ProductDto
import com.example.domain.Result
import com.example.data.source.remote.service.TrovesApiService

class RemoteDatasourceImpl(
    private val trovesApiService: TrovesApiService
): RemoteDatasource {
    override suspend fun createProduct(productDto: ProductDto): Result<ProductDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllProducts(): Result<ProductResponse> {
        TODO("Not yet implemented")
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