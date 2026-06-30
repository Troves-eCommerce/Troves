package com.troves.domain.usecase.products

import com.troves.domain.entity.Product

class FilterProductsUseCase {
    operator fun invoke(
        products: List<Product>,
        brandIds: Set<String>,
        categoryNames: Set<String>,
    ): List<Product> {
        var result = products

        if (brandIds.isNotEmpty()) {
            result = result.filter { it.vendor in brandIds }
        }

        if (categoryNames.isNotEmpty()) {
            result = result.filter { product ->
                categoryNames.any { product.title.contains(it, ignoreCase = true) }
            }
        }

        return result
    }
}
