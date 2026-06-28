package com.troves.domain.repository

import com.troves.domain.Result
import com.troves.domain.entity.Ad
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category
import com.troves.domain.entity.Product

interface TrovesRepository {
    suspend fun getAllProducts(): Result<List<Product>>
    suspend fun getBrands(): Result<List<Brand>>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getAds(): Result<List<Ad>>
}
