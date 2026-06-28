package com.troves.domain.home

import com.troves.domain.Product
import com.troves.domain.Result

/**
 * Aggregates every piece of data the Home screen needs.
 * The presentation layer depends on this contract, never on a concrete source,
 * so a fake implementation can stand in until the real Shopify-backed one lands.
 */
interface HomeRepository {
    suspend fun getAds(): Result<List<Ad>>
    suspend fun getBrands(): Result<List<Brand>>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getJustForYouProducts(): Result<List<Product>>
    suspend fun getTrendingProducts(): Result<List<Product>>
}
