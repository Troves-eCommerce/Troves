package com.troves.data.source.remote.service.apollo

import com.apollographql.apollo.ApolloClient
import com.troves.data.source.remote.service.ShopifyApiService
import com.troves.data.source.remote.service.apollo.graphql.storefront.CreateCustomerMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.CustomerAccessTokenCreateMutation
import com.troves.data.source.remote.service.apollo.graphql.storefront.type.CustomerCreateInput
import com.troves.data.source.remote.service.apollo.mapper.toDto
import com.troves.data.source.remote.service.apollo.util.runMutation
import com.troves.data.source.remote.service.apollo.util.toQueryOptional
import com.troves.data.source.remote.service.shopify_dtos.AccessTokenResponse
import com.troves.data.source.remote.service.shopify_dtos.ShopifyUserResponse
import com.troves.domain.entity.Customer
import com.troves.domain.utils.Result

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
class ShopifyApiServiceImpl(
    private val apolloClient: ApolloClient
): ShopifyApiService {
    override suspend fun createCustomer(
        customer: Customer
    ): Result<ShopifyUserResponse> =
        apolloClient.runMutation(
            CreateCustomerMutation(
                input = CustomerCreateInput(
                    firstName = customer.firstName.toQueryOptional(),
                    lastName = customer.lastName.toQueryOptional(),
                    email = customer.email,
                    phone = customer.phone.toQueryOptional(),
                    password = customer.password,
                    acceptsMarketing = com.apollographql.apollo.api.Optional.present(true)
                )
            )
        ) { data ->
            data.customerCreate.toDto()
        }

    override suspend fun getCustomerAccessToken(
        email: String,
        password: String
    ): Result<AccessTokenResponse> =
        apolloClient.runMutation(
            CustomerAccessTokenCreateMutation(
                email = email,
                password = password
            )
        ) { data ->
            data.customerAccessTokenCreate.toDto()
        }
}
