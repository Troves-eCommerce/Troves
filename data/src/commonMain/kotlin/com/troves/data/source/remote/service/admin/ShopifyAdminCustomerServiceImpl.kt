package com.troves.data.source.remote.service.admin

import com.troves.data.source.remote.service.ktor.getResults
import com.troves.domain.utils.Result
import com.troves.domain.utils.getOrNull
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path


class ShopifyAdminCustomerServiceImpl(
    private val client: HttpClient,
) : ShopifyAdminCustomerService {

    override suspend fun findCustomerIdByEmail(email: String): Long? {
        val result: Result<AdminCustomerSearchResponse> = client.getResults {
            method = HttpMethod.Get
            url { path("customers/search.json") }
            parameter("query", "email:$email")
        }
        val customers = result.getOrNull()?.customers.orEmpty()
        return customers.firstOrNull { it.email.equals(email, ignoreCase = true) }?.id
            ?: customers.firstOrNull()?.id
    }

    override suspend fun setCustomerPassword(customerId: Long, password: String): Result<Unit> {
        val result: Result<AdminSingleCustomerResponse> = client.getResults {
            method = HttpMethod.Put
            url { path("customers/$customerId.json") }
            contentType(ContentType.Application.Json)
            setBody(
                AdminCustomerUpdateRequest(
                    AdminCustomerUpdateBody(
                        id = customerId,
                        password = password,
                        passwordConfirmation = password,
                    )
                )
            )
        }
        return when (result) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> result
            is Result.Loading -> Result.Loading
        }
    }
}
