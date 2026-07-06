package com.troves.data.source.remote.ai

import com.troves.data.source.remote.ai.dto.AiChatRequestDto
import com.troves.data.source.remote.ai.dto.AiChatResponseDto
import com.troves.data.source.remote.ai.dto.AiErrorDto
import com.troves.domain.utils.RateLimitException
import com.troves.domain.utils.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.path


interface AiApiService {
    suspend fun chat(request: AiChatRequestDto): Result<AiChatResponseDto>
}

class AiApiServiceImpl(
    private val client: HttpClient,
) : AiApiService {

    override suspend fun chat(request: AiChatRequestDto): Result<AiChatResponseDto> = try {
        val response = client.post {
            url { path("chat") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        when (response.status) {
            HttpStatusCode.OK -> Result.Success(response.body())
            HttpStatusCode.TooManyRequests -> {
                val retryAfter = runCatching { response.body<AiErrorDto>().retryAfter }.getOrNull() ?: 3600
                Result.Error(RateLimitException(retryAfter))
            }
            else -> Result.Error(Throwable("${response.status}: ${response.bodyAsText()}"))
        }
    } catch (e: Exception) {
        Result.Error(e)
    }
}
