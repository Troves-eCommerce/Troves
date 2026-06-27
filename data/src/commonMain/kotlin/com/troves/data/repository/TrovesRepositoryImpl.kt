package com.troves.data.repository

import com.troves.data.source.remote.RemoteDatasource
import com.troves.domain.Product
import com.troves.domain.Result
import com.troves.domain.TrovesRepository

class TrovesRepositoryImpl(
    private val remoteDataSource: RemoteDatasource
) : TrovesRepository {

    override suspend fun getAllProducts(): Result<List<Product>> {
        return when (val result = remoteDataSource.getAllProducts()) {
            is Result.Success -> {
                val response = result.value as com.troves.data.source.remote.dto.ProductResponse
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