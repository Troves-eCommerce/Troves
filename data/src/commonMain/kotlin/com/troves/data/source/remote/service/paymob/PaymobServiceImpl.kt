package com.troves.data.source.remote.service.paymob

import com.troves.data.source.remote.service.ktor.getResults
import com.troves.data.source.remote.service.shopify_dtos.ShopifyUserResponse
import com.troves.domain.utils.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders.From
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.util.reflect.TypeInfo
import kotlin.collections.buildMap

/**
 * Copyright (c) 2026 Wahid Ali Wahid Hussien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Author: Wahid Ali Wahid Hussien
 * Created: 05/07/2026
 */
class PaymobServiceImpl(
    private val httpClient: HttpClient
) : PaymobApiService {
    override suspend fun getClientSecret(cartId: String): Result<ClientSecretResponse> {
        return httpClient.getResults {
            contentType(ContentType.Application.Json)
            method = HttpMethod.Post
            url {
                path("functions/v1/create-checkout")
            }
            setBody(body = buildMap {
                put("cartId", cartId)
            }, bodyType = TypeInfo(Map::class))
        }
    }
}