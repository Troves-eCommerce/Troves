package com.troves.data.source.remote.service

import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.dto.Collection
import com.troves.data.source.remote.dto.CollectionImage
import com.troves.data.source.remote.dto.CustomCollectionResponse
import com.troves.data.source.remote.dto.MarketingEventsResponse
import com.troves.data.source.remote.dto.ProductDto
import com.troves.data.source.remote.dto.ProductResponse
import com.troves.data.source.remote.dto.SingleProductResponse
import com.troves.domain.Result
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path
import io.ktor.client.request.url
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TrovesApiServiceImpl(
    private val ktorClient: HttpClient
) : TrovesApiService {
    override suspend fun createProduct(productDto: ProductDto): Result<ProductDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllProducts(): Result<ProductResponse> =
        ktorClient.getResults {
            method = HttpMethod.Get
            url { path("products.json") }
        }

    override suspend fun getProductImages(productId: String): Result<List<CollectionImage>> {
        return ktorClient.getResults {
            method = HttpMethod.Get
            url{
                path("products/$productId/images.json")
            }
        }
    }

    override suspend fun getProductById(productId: String): Result<SingleProductResponse> {
        return ktorClient.getResults {
            method = HttpMethod.Get
            url{
                path("products/$productId.json")
            }
        }
    }

    override suspend fun updateProduct(productId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productDto: ProductDto) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllBrands(): Result<Collection> =
        ktorClient.getResults {
            method = HttpMethod.Get
            url { path("smart_collections.json") }
        }

    override suspend fun getCategory(): Result<CustomCollectionResponse> =
        ktorClient.getResults {
            method = HttpMethod.Get
            url { path("custom_collections.json") }
        }


    override suspend fun getAllEventsById(eventId: String): MarketingEventsResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getCountries(): Result<List<com.troves.data.source.remote.dto.RestCountryDto>> {
        val result = ktorClient.getResults<com.troves.data.source.remote.dto.RestCountriesV5Response> {
            method = HttpMethod.Get
            url {
                protocol = io.ktor.http.URLProtocol.HTTPS
                host = "api.restcountries.com"
                pathSegments = listOf("countries", "v5")
                parameters.append("response_fields", "names.common")
                parameters.append("limit", "100")
            }
            header("Authorization", "Bearer ${com.troves.data.BuildKonfig.REST_COUNTRIES_API_KEY}")
        }
        return when (result) {
            is Result.Success -> Result.Success(result.value.data?.objects ?: emptyList())
            is Result.Error -> Result.Error(result.throwable)
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun getCities(country: String): Result<com.troves.data.source.remote.dto.CountriesNowCitiesDto> {
        return ktorClient.getResults {
            method = HttpMethod.Post
            url {
                protocol = io.ktor.http.URLProtocol.HTTPS
                host = "countriesnow.space"
                pathSegments = listOf("api", "v0.1", "countries", "cities")
            }
            contentType(ContentType.Application.Json)
            setBody(com.troves.data.source.remote.dto.CountriesNowRequestDto(country = country))
        }
    }
}