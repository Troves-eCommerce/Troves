package com.troves.data.source.remote

import com.troves.data.source.remote.dto.CartItemDto
import com.troves.data.source.remote.service.ktor.dto.CollectionImage
import com.troves.data.source.remote.service.ktor.dto.Collection
import com.troves.data.source.remote.service.ktor.dto.CustomCollectionResponse
import com.troves.data.source.remote.service.ktor.dto.MarketingEventsResponse
import com.troves.data.source.remote.service.ktor.dto.ProductDto
import com.troves.data.source.remote.service.ktor.dto.ProductResponse
import com.troves.data.source.remote.service.ktor.dto.SingleProductResponse
import com.troves.data.source.remote.service.ktor.dto.WishlistDto
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.utils.Result

interface RemoteDatasource {
    //region product
    suspend fun createProduct(productDto: ProductDto): Result<ProductDto>
    suspend fun getAllProducts(): Result<ProductResponse>
    suspend fun getProductsByQuery(queryMap: Map<String, String>): Result<ProductResponse>
    suspend fun searchProducts(params: ProductSearchParams): Result<List<Product>>
    suspend fun getProductsByVendor(vendorName: String): Result<List<Product>>
    suspend fun getProductsByCollection(collectionId: String): Result<List<Product>>


    suspend fun getProductImages(productId: String):Result<List<CollectionImage>>
    suspend fun getProductById(productId: String): Result<SingleProductResponse>
    suspend fun updateProduct(productId: String)
    suspend fun deleteProduct(productDto: ProductDto)
    // endregion


    //region brands

    suspend fun getAllBrands(): Result<Collection>
    suspend fun getCategory(): Result<CustomCollectionResponse>

    //endregion

    //region
    suspend fun getAllEventsById(eventId: String): MarketingEventsResponse
    //endregion

    //region wishlist
    suspend fun getWishlist(userId: String): Result<List<WishlistDto>>
    suspend fun addToWishlist(userId: String, item: WishlistDto): Result<Unit>
    suspend fun removeFromWishlist(userId: String, productId: Long): Result<Unit>
    //endregion
    //region cart
    suspend fun getCart(userId: String): Result<List<CartItemDto>>
    suspend fun addToCart(userId: String, item: CartItemDto): Result<Unit>
    suspend fun removeFromCart(userId: String, productId: Long): Result<Unit>
    suspend fun clearCart(userId: String): Result<Unit>
    //endregion

    suspend fun getDiscountCodes(): Result<List<com.troves.domain.entity.DiscountCode>>
}