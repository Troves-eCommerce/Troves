package com.example.data.repository

import com.example.data.source.remote.RemoteDatasource
import com.example.domain.Product
import com.example.domain.Result
import com.example.domain.TrovesRepository

class TrovesRepositoryImpl(
    private val remoteDataSource: RemoteDatasource
) : TrovesRepository {

    override suspend fun getAllProducts(): Result<List<Product>> {
        return when (val result = remoteDataSource.getAllProducts()) {
            is Result.Success -> {
                val response = result.value as com.example.data.source.remote.dto.ProductResponse
                val products = response.products
                    ?.filterNotNull()
                    ?.map { dto ->
                        Product(
                            id        = dto.id ?: 0L,
                            title     = dto.title.orEmpty(),
                            vendor    = dto.vendor.orEmpty(),
                            price     = dto.variants?.firstOrNull()?.price.orEmpty(),
                            imageUrl  = dto.image?.src,
                            status    = dto.status.orEmpty()
                        )
                    }
                    .orEmpty()
                Result.Success(products)
            }
            is Result.Error   -> Result.Error(result.throwable)
            is Result.Loading -> Result.Loading
        }
    }
}