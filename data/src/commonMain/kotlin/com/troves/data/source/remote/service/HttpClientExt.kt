package com.troves.data.source.remote.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import com.troves.domain.utils.Result

suspend inline fun <reified T> HttpClient.getResults(
    block: HttpRequestBuilder.() -> Unit
): Result<T> = try {
    val response = request(block)
    if (response.status == HttpStatusCode.OK) {
        Result.Success(response.body())
    } else {
        Result.Error(Throwable("${response.status}: ${response.bodyAsText()}"))
    }
} catch (e: Exception) {
    Result.Error(e)
}