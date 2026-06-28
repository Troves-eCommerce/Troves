package com.troves.data.source.remote.service
import com.troves.data.source.remote.dto.Collection
import com.troves.data.source.remote.dto.CollectionImage
import com.troves.data.source.remote.dto.MarketingEventsResponse
import com.troves.data.source.remote.dto.ProductDto
import com.troves.data.source.remote.dto.ProductResponse

interface TrovesApiService {
    suspend fun createProduct(productDto: ProductDto): com.troves.domain.Result<ProductDto>
    suspend fun getAllProducts(): com.troves.domain.Result<ProductResponse>
    suspend fun getProductImages(productId: String): com.troves.domain.Result<List<CollectionImage>>
    suspend fun getProductById(productId: String): com.troves.domain.Result<ProductResponse>
    suspend fun updateProduct(productId: String)
    suspend fun deleteProduct(productDto: ProductDto)
    // endregion


    //region brands

    suspend fun getAllBrands(): com.troves.domain.Result<Collection>
    suspend fun getCategory(): Result<Collection>

    //endregion

    //region
    suspend fun getAllEventsById(eventId: String): MarketingEventsResponse
}