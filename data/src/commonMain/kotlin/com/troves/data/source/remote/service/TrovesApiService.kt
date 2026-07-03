package com.troves.data.source.remote.service

import com.troves.data.source.remote.service.ktor.dto.Collection
import com.troves.data.source.remote.service.ktor.dto.CollectionImage
import com.troves.data.source.remote.service.ktor.dto.CustomCollectionResponse
import com.troves.data.source.remote.service.ktor.dto.MarketingEventsResponse
import com.troves.data.source.remote.service.ktor.dto.ProductDto
import com.troves.data.source.remote.service.ktor.dto.ProductResponse
import com.troves.data.source.remote.service.ktor.dto.SingleProductResponse
import com.troves.domain.entity.Address
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.utils.Result

interface TrovesApiService {
    //region products
    suspend fun createProduct(productDto: ProductDto): Result<ProductDto>
    suspend fun getAllProducts(): Result<ProductResponse>
    suspend fun getProductsByQuery(queryMap: Map<String, String>): Result<ProductResponse>
    suspend fun searchProducts(params: ProductSearchParams): Result<List<Product>>
    suspend fun getProductsByVendor(vendorName: String): Result<List<Product>>
    suspend fun getProductsByCollection(collectionId: String): Result<List<Product>>

    suspend fun getProductImages(productId: String): Result<List<CollectionImage>>
    suspend fun getProductById(productId: String): Result<Product>
    suspend fun updateProduct(productId: String)
    suspend fun deleteProduct(productDto: ProductDto)
    // endregion


    //region brands

    suspend fun getAllBrands(): Result<Collection>
    suspend fun getCategory(): Result<CustomCollectionResponse>

    //endregion

    //region events
    suspend fun getAllEventsById(eventId: String): MarketingEventsResponse
    //endregion

    suspend fun getDiscountCodes(): Result<List<com.troves.domain.entity.DiscountCode>>


    suspend fun createOrder(
        email: String?,
        address: Address,
        lineItems: List<Pair<String, Int>>,
    ): Result<String>
}