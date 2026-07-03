package com.troves.data.source.remote.service.apollo.mapper

import com.troves.data.source.remote.service.apollo.graphql.storefront.CreateCustomerMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.CustomerAccessTokenCreateMutation
import com.troves.data.source.remote.service.shopify_dtos.AccessTokenResponse
import com.troves.data.source.remote.service.shopify_dtos.Customer
import com.troves.data.source.remote.service.shopify_dtos.CustomerAccessToken
import com.troves.data.source.remote.service.shopify_dtos.CustomerUserError
import com.troves.data.source.remote.service.shopify_dtos.ShopifyUserResponse

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
 * Created: 03/07/2026
 */

internal fun CreateCustomerMutation.CustomerCreate?.toDto(): ShopifyUserResponse =
    ShopifyUserResponse(
        customer = this?.customer?.let {
            Customer(
                id = it.id,
                firstName = it.firstName,
                lastName = it.lastName,
                email = it.email,
            )
        },
        customerUserErrors = this?.customerUserErrors?.map {
            CustomerUserError(
                field = it.field?.joinToString("."),
                message = it.message,
                code = it.code?.toString()
            )
        }
    )

internal fun CustomerAccessTokenCreateMutation.CustomerAccessTokenCreate?.toDto(): AccessTokenResponse =
    AccessTokenResponse(
        customerAccessToken = this?.customerAccessToken?.let {
            CustomerAccessToken(
                accessToken = it.accessToken,
                expiresAt = it.expiresAt.toString()
            )
        },
        customerUserErrors = this?.customerUserErrors?.map {
            CustomerUserError(
                field = it.field?.joinToString("."),
                message = it.message,
                code = it.code?.toString()
            )
        }
    )
