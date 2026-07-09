package com.troves.domain.repository

import com.troves.domain.entity.Ad
import com.troves.domain.entity.Address
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Cart
import com.troves.domain.entity.Category
import com.troves.domain.entity.DiscountCode
import com.troves.domain.entity.Order
import com.troves.domain.entity.OrderSummary
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.entity.Review
import com.troves.domain.entity.SurveyAnswers
import com.troves.domain.entity.SurveyRecommendedItem
import com.troves.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface TrovesRepository {
    // ── Catalogue ───────────────────────────────────────────────────────────
    suspend fun getAllProducts(): Result<List<Product>>
    suspend fun getProductsByQuery(queryMap: Map<String, String>): Result<List<Product>>
    suspend fun searchProducts(params: ProductSearchParams): Result<List<Product>>
    suspend fun getProductsByVendor(vendorName: String): Result<List<Product>>
    suspend fun getProductsByCollection(collectionId: Long): Result<List<Product>>
    suspend fun getProductById(productId: String): Result<Product>
    suspend fun getBrands(): Result<List<Brand>>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getAds(): Result<List<Ad>>
    suspend fun getDiscountCodes(): Result<List<DiscountCode>>
    suspend fun getSurveyRecommendations(surveyAnswers: SurveyAnswers): Result<List<SurveyRecommendedItem>>

    // ── Settings ────────────────────────────────────────────────────────────
    val selectedLanguage: Flow<String>
    val themeMode: Flow<String>
    val selectedCurrency: Flow<String>
    val isCartHintShown: Flow<Boolean>
    suspend fun setSelectedLanguage(language: String)
    suspend fun setThemeMode(mode: String)
    suspend fun setSelectedCurrency(currency: String)
    suspend fun setCartHintShown(shown: Boolean)

    // ── Cart (Shopify = source of truth) ─────────────────────────────────────
    val cart: Flow<Cart?>
    suspend fun addToCart(variantId: String, quantity: Int): Cart
    suspend fun updateQuantity(lineId: String, quantity: Int): Cart
    suspend fun removeFromCart(lineId: String): Cart
    suspend fun removeAllItems(): Cart
    suspend fun applyDiscountCodes(codes: List<String>): Cart
    suspend fun refreshCart()
    suspend fun clearCart()
    suspend fun clearLocal()

    // ── Checkout / orders ─────────────────────────────────────────────────────
    suspend fun getDefaultAddress(): Address?
    suspend fun getOrders(): List<OrderSummary>
    suspend fun getOrderById(orderId: String): Order?
    suspend fun placeCodOrder(cart: Cart, address: Address): String
    suspend fun attachAddressToCart(cartId: String, address: Address)

    // ── Product reviews (Firestore) ───────────────────────────────────────────
    suspend fun getReviews(productId: String): Result<List<Review>>
    suspend fun submitReview(productId: String, review: Review): Result<Unit>
}
