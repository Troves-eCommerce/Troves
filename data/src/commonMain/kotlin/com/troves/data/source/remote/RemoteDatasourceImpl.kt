package com.troves.data.source.remote

import com.troves.data.source.remote.dto.Collection
import com.troves.data.source.remote.dto.CollectionImage
import com.troves.data.source.remote.dto.CustomCollectionResponse
import com.troves.data.source.remote.dto.MarketingEventsResponse
import com.troves.data.source.remote.dto.ProductDto
import com.troves.data.source.remote.dto.ProductResponse
import com.troves.data.source.remote.dto.SingleProductResponse
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.utils.Result

class RemoteDatasourceImpl(
    private val trovesApiService: TrovesApiService
): RemoteDatasource {
    override suspend fun createProduct(productDto: ProductDto): Result<ProductDto> {
        TODO("Not yet implemented")
    }
    override suspend fun getAllProducts(): Result<ProductResponse> {
        return trovesApiService.getAllProducts()
    }

    override suspend fun getProductsByQuery(queryMap: Map<String, String>): Result<ProductResponse> {
        return trovesApiService.getProductsByQuery(queryMap = queryMap)
    }

    override suspend fun searchProducts(params: ProductSearchParams): Result<List<Product>> {
      return  trovesApiService.searchProducts(params = params)
    }

    override suspend fun getProductImages(productId: String): Result<List<CollectionImage>> {
        return trovesApiService.getProductImages(productId = productId)
    }

    override suspend fun getProductById(productId: String): Result<SingleProductResponse> {
        return trovesApiService.getProductById(productId = productId)
    }

    override suspend fun updateProduct(productId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productDto: ProductDto) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllBrands(): Result<Collection> {
        return trovesApiService.getAllBrands()
    }

    override suspend fun getCategory(): Result<CustomCollectionResponse> {
        return trovesApiService.getCategory()
    }

    override suspend fun getAllEventsById(eventId: String): MarketingEventsResponse {
        TODO("Not yet implemented")
    }


}