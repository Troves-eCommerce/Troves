package com.troves.data.repository

import com.troves.data.mapper.toBrand
import com.troves.data.mapper.toCategory
import com.troves.data.mapper.toDomain
import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.util.applyAppLocale
import com.troves.domain.entity.Ad
import com.troves.domain.entity.Brand
import com.troves.domain.entity.Category
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result
import com.troves.domain.utils.fold
import com.troves.domain.utils.getOrElse
import com.troves.domain.utils.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.coroutines.IO

class TrovesRepositoryImpl(
    private val remoteDataSource: RemoteDatasource,
    private val dataSource: TrovesPreferences,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TrovesRepository {

    override suspend fun getAllProducts(): Result<List<Product>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getAllProducts().map { response ->
                response.products
                    ?.map { it.toDomain() }
                    .orEmpty()
            }
        }
    }

    override suspend fun getProductsByQuery(queryMap: Map<String, String>): Result<List<Product>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getProductsByQuery(queryMap = queryMap).map { response ->
                response.products?.map { it.toDomain() }.orEmpty()
            }
        }
    }

    override suspend fun searchProducts(params: ProductSearchParams): Result<List<Product>> {
        return try {
            withContext(coroutineDispatcher) {
                var products =
                    remoteDataSource.searchProducts(params = params).getOrElse { emptyList() }
                if (!params.query.isNullOrBlank()) {
                    products = products.filter {
                        it.title.contains(params.query ?: "", ignoreCase = true)
                    }
                }
                Result.Success(products)
            }
        } catch (e: IOException) {
            Result.Error(e)
        }
    }

    override suspend fun getProductsByVendor(vendorName: String): Result<List<Product>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getProductsByVendor(vendorName = vendorName)
        }
    }

    override suspend fun getProductsByCollection(collectionId: Long): Result<List<Product>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getProductsByCollection(collectionId = collectionId.toString())
        }
    }

    override suspend fun getProductById(productId: String): Result<Product> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getProductById(productId = productId).fold(
                onSuccess = { response ->
                    response.product
                        ?.toDomain()
                        ?.let { Result.Success(it) }
                        ?: Result.Error(Exception("Product not found: $productId"))
                },
                onError = { Result.Error(it) },
                onLoading = { Result.Loading }
            )
        }
    }

    override suspend fun getBrands(): Result<List<Brand>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getAllBrands().map { collection ->
                collection.smartCollections
                    ?.filterNotNull()
                    ?.map { it.toBrand() }
                    .orEmpty()
            }
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getCategory().map { response ->
                response.customCollections
                    ?.map { it.toCategory() }
                    .orEmpty()
            }
        }
    }

    override suspend fun getAds(): Result<List<Ad>> = Result.Success(FAKE_ADS)

    override val selectedLanguage: Flow<String> = dataSource.selectedLanguage
    override val themeMode: Flow<String> = dataSource.themeMode
    override val selectedCurrency: Flow<String> = dataSource.selectedCurrency

    override suspend fun setSelectedLanguage(language: String) {
        dataSource.setSelectedLanguage(language)

        withContext(Dispatchers.Main) {
            applyAppLocale(language)
        }
    }

    override suspend fun setThemeMode(mode: String) =
        dataSource.setThemeMode(mode)

    override suspend fun setSelectedCurrency(currency: String) =
        dataSource.setSelectedCurrency(currency)

    override suspend fun getCountries(): Result<List<String>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getCountries().map { dtoList ->
                dtoList.mapNotNull { it.nameCommon ?: it.names?.common }.sorted()
            }
        }
    }

    override suspend fun getCities(countryName: String): Result<List<String>> {
        return withContext(coroutineDispatcher) {
            remoteDataSource.getCities(countryName).map { dto ->
                dto.data?.sorted() ?: emptyList()
            }
        }
    }

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