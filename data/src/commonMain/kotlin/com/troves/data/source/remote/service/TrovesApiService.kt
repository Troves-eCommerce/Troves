package com.troves.data.source.remote.service
import com.troves.data.source.remote.dto.Collection
import com.troves.data.source.remote.dto.CollectionImage
import com.troves.data.source.remote.dto.CustomCollectionResponse
import com.troves.data.source.remote.dto.MarketingEventsResponse
import com.troves.data.source.remote.dto.ProductDto
import com.troves.data.source.remote.dto.ProductResponse
import com.troves.data.source.remote.dto.SingleProductResponse

interface TrovesApiService {
    //region products
    suspend fun createProduct(productDto: ProductDto): com.troves.domain.Result<ProductDto>
    suspend fun getAllProducts(): com.troves.domain.Result<ProductResponse>
    suspend fun getProductImages(productId: String): com.troves.domain.Result<List<CollectionImage>>
    suspend fun getProductById(productId: String): com.troves.domain.Result<SingleProductResponse>
    suspend fun updateProduct(productId: String)
    suspend fun deleteProduct(productDto: ProductDto)
    // endregion


    //region brands

    suspend fun getAllBrands(): com.troves.domain.Result<Collection>
    suspend fun getCategory(): com.troves.domain.Result<CustomCollectionResponse>
    suspend fun getCountries(): com.troves.domain.Result<List<com.troves.data.source.remote.dto.RestCountryDto>>
    suspend fun getCities(country: String): com.troves.domain.Result<com.troves.data.source.remote.dto.CountriesNowCitiesDto>

    //endregion

    //region events
    suspend fun getAllEventsById(eventId: String): MarketingEventsResponse
    //endregion
}