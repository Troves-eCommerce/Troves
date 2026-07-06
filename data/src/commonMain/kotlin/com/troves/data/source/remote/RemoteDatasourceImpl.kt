package com.troves.data.source.remote

import com.troves.data.source.remote.dto.CartItemDto
import com.troves.data.source.remote.dto.UserProfileDto
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.data.source.remote.service.ktor.dto.Collection
import com.troves.data.source.remote.service.ktor.dto.CollectionImage
import com.troves.data.source.remote.service.ktor.dto.CustomCollectionResponse
import com.troves.data.source.remote.service.ktor.dto.MarketingEventsResponse
import com.troves.data.source.remote.service.ktor.dto.ProductDto
import com.troves.data.source.remote.service.ktor.dto.ProductResponse
import com.troves.data.source.remote.service.ktor.dto.SingleProductResponse
import com.troves.data.source.remote.service.ktor.dto.WishlistDto
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

    override suspend fun getProductsByVendor(vendorName: String): Result<List<Product>> {
        return trovesApiService.getProductsByVendor(vendorName = vendorName)
    }

    override suspend fun getProductsByCollection(collectionId: String): Result<List<Product>> {
        return trovesApiService.getProductsByCollection(collectionId = collectionId)
    }

    override suspend fun getProductImages(productId: String): Result<List<CollectionImage>> {
        return trovesApiService.getProductImages(productId = productId)
    }

    override suspend fun getProductById(productId: String): Result<Product> {
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

    private fun cartCollection(userId: String) =
        firestore.collection("users").document(userId).collection("cart")

    override suspend fun getCart(userId: String): Result<List<CartItemDto>> {
        return try {
            val snapshot = cartCollection(userId).get()
            val items = snapshot.documents.map { it.data(CartItemDto.serializer()) }
            Result.Success(items)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addToCart(userId: String, item: CartItemDto): Result<Unit> {
        return try {
            cartCollection(userId).document(item.id.toString()).set(item)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeFromCart(userId: String, productId: Long): Result<Unit> {
        return try {
            cartCollection(userId).document(productId.toString()).delete()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun clearCart(userId: String): Result<Unit> {
        return try {
            val snapshot = cartCollection(userId).get()
            snapshot.documents.forEach { it.reference.delete() }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun userDoc(userId: String) =
        firestore.collection("users").document(userId)

    override suspend fun getUserCartId(userId: String): String? {
        return try {
            val snapshot = userDoc(userId).get()
            if (snapshot.exists) snapshot.get<String?>("cartId") else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun setUserCartId(userId: String, cartId: String) {
        userDoc(userId).set(UserProfileDto(cartId = cartId), merge = true)
    }

    override suspend fun clearUserCartId(userId: String) {
        userDoc(userId).set(UserProfileDto(cartId = null), merge = true)
    }

    override suspend fun getDiscountCodes(): Result<List<com.troves.domain.entity.DiscountCode>> {
        return trovesApiService.getDiscountCodes()
    }

}