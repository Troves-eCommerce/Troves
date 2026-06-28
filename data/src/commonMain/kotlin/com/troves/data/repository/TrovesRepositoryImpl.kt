package com.troves.data.repository

import com.troves.data.mapper.toBrand
import com.troves.data.mapper.toCategory
import com.troves.data.mapper.toDomain
import com.troves.data.source.remote.RemoteDatasource
import com.troves.domain.Result
import com.troves.domain.entity.Ad
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category
import com.troves.domain.entity.Product
import com.troves.domain.map
import com.troves.domain.repository.TrovesRepository

class TrovesRepositoryImpl(
    private val remoteDataSource: RemoteDatasource
) : TrovesRepository {

    override suspend fun getAllProducts(): Result<List<Product>> =
        remoteDataSource.getAllProducts().map { response ->
            response.products
                ?.filterNotNull()
                ?.map { it.toDomain() }
                .orEmpty()
        }

    override suspend fun getBrands(): Result<List<Brand>> =
        remoteDataSource.getAllBrands().map { collection ->
            collection.smartCollections
                ?.filterNotNull()
                ?.map { it.toBrand() }
                .orEmpty()
        }

    override suspend fun getCategories(): Result<List<Category>> =
        remoteDataSource.getCategory().map { response ->
            response.customCollections
                ?.map { it.toCategory() }
                .orEmpty()
        }

    override suspend fun getAds(): Result<List<Ad>> = Result.Success(FAKE_ADS)

    private companion object {
        val FAKE_ADS = listOf(
            Ad(
                id = 1,
                titleTop = "30% DISCOUNT",
                titleBottom = "Today special",
                description = "Get discount for every order, only valid for today.",
            ),
            Ad(
                id = 2,
                titleTop = "NEW ARRIVALS",
                titleBottom = "Summer 2026",
                description = "Fresh styles just landed. Explore the latest collection.",
            ),
            Ad(
                id = 3,
                titleTop = "FREE SHIPPING",
                titleBottom = "Orders over \$50",
                description = "Shop more, save more with free delivery on big orders.",
            ),
        )
    }
}
