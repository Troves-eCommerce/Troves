package com.troves.data.source.remote.service.apollo

import com.troves.data.source.remote.dto.Collection
import com.troves.data.source.remote.dto.CollectionImage
import com.troves.data.source.remote.dto.CustomCollectionResponse
import com.troves.data.source.remote.dto.MarketingEventsResponse
import com.troves.data.source.remote.dto.ProductDto
import com.troves.data.source.remote.dto.ProductResponse
import com.troves.data.source.remote.dto.SingleProductResponse
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
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
 * Created: 02/07/2026
 */
class ApolloTrovesApiServiceImpl(

): TrovesApiService{
    override suspend fun createProduct(productDto: ProductDto): Result<ProductDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllProducts(): Result<ProductResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductsByQuery(queryMap: Map<String, String>): Result<ProductResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun searchProducts(params: ProductSearchParams): Result<List<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductImages(productId: String): Result<List<CollectionImage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(productId: String): Result<SingleProductResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun updateProduct(productId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productDto: ProductDto) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllBrands(): Result<Collection> {
        TODO("Not yet implemented")
    }

    override suspend fun getCategory(): Result<CustomCollectionResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllEventsById(eventId: String): MarketingEventsResponse {
        TODO("Not yet implemented")
    }

}