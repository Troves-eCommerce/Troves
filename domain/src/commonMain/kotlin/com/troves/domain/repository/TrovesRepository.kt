package com.troves.domain.repository

import com.troves.domain.entity.Ad
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface TrovesRepository {
    suspend fun getAllProducts(): Result<List<Product>>
    suspend fun getProductsByQuery(queryMap: Map<String, String>): Result<List<Product>>
    suspend fun searchProducts(params: ProductSearchParams): Result<List<Product>>
    suspend fun getProductsByVendor(vendorName: String): Result<List<Product>>
    suspend fun getProductsByCollection(collectionId: Long): Result<List<Product>>
    suspend fun getProductById(productId: String): Result<Product>
    suspend fun getBrands(): Result<List<Brand>>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getAds(): Result<List<Ad>>
    val selectedLanguage: Flow<String>
    val themeMode: Flow<String>
    val selectedCurrency: Flow<String>
    suspend fun setSelectedLanguage(language: String)
    suspend fun setThemeMode(mode: String)
    suspend fun setSelectedCurrency(currency: String)

    suspend fun getCountries(): Result<List<String>>
    suspend fun getCities(countryName: String): Result<List<String>>
}
