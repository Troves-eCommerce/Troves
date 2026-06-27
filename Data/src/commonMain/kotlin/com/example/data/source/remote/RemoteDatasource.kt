package com.example.data.source.remote

import com.example.data.source.remote.dto.Collection
import com.example.data.source.remote.dto.CollectionImage
import com.example.data.source.remote.dto.MarketingEventsResponse
import com.example.data.source.remote.dto.ProductResponse
import com.example.data.source.remote.dto.ProductDto
import com.example.data.source.remote.service.Result

interface RemoteDatasource {
    //region product
    suspend fun createProduct(productDto: ProductDto): Result<ProductDto>
    suspend fun getAllProducts(): Result<ProductResponse>
    suspend fun getProductImages(productId: String):Result<List<CollectionImage>>
    suspend fun getProductById(productId: String): Result<ProductResponse>
    suspend fun updateProduct(productId: String)
    suspend fun deleteProduct(productDto: ProductDto)
    // endregion


    //region brands

    suspend fun getAllBrands(): Result<Collection>
    suspend fun getCategory(): Result<Collection>

    //endregion

    //region
    suspend fun getAllEventsById(eventId: String): MarketingEventsResponse
    //endregion






}