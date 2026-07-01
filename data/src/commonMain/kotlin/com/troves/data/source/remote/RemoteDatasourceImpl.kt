package com.troves.data.source.remote

import com.troves.data.source.remote.dto.Collection
import com.troves.data.source.remote.dto.CollectionImage
import com.troves.data.source.remote.dto.CustomCollectionResponse
import com.troves.data.source.remote.dto.MarketingEventsResponse
import com.troves.data.source.remote.dto.ProductDto
import com.troves.data.source.remote.dto.ProductResponse
import com.troves.data.source.remote.dto.SingleProductResponse
import com.troves.data.source.remote.dto.WishlistDto
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.utils.Result
import dev.gitlive.firebase.firestore.FirebaseFirestore

class RemoteDatasourceImpl(
    private val trovesApiService: TrovesApiService,
    private val firestore: FirebaseFirestore,
) : RemoteDatasource {
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
    private fun wishlistCollection(userId: String) =
        firestore.collection("users").document(userId).collection("wishlist")

    override suspend fun getWishlist(userId: String): Result<List<WishlistDto>> {
        return try {
            val snapshot = wishlistCollection(userId).get()
            val items = snapshot.documents.map { it.data(WishlistDto.serializer()) }
            Result.Success(items)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addToWishlist(userId: String, item: WishlistDto): Result<Unit> {
        return try {
            wishlistCollection(userId).document(item.id.toString()).set(item)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeFromWishlist(userId: String, productId: Long): Result<Unit> {
        return try {
            wishlistCollection(userId).document(productId.toString()).delete()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}