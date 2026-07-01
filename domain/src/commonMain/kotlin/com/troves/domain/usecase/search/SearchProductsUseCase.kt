package com.troves.domain.usecase.search

import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductSearchParams
import com.troves.domain.repository.TrovesRepository
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
 * Created: 01/07/2026
 */
class SearchProductsUseCase(
    private val trovesRepository: TrovesRepository
){
    suspend operator fun invoke(params: ProductSearchParams): Result<List<Product>> {
        return trovesRepository.searchProducts(params)
    }
}