package com.troves.domain.usecase.products

import com.troves.domain.entity.Product

enum class ProductSortOption {
    DEFAULT,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    BEST_SELLER,
    GROUP_BY_SUB_CATEGORY,
}

class SortProductsUseCase {
    operator fun invoke(
        products: List<Product>,
        sort: ProductSortOption,
    ): List<Product> = when (sort) {
        ProductSortOption.DEFAULT -> products
        ProductSortOption.PRICE_LOW_TO_HIGH ->
            products.sortedBy { it.price.toDoubleOrNull() ?: 0.0 }
        ProductSortOption.PRICE_HIGH_TO_LOW ->
            products.sortedByDescending { it.price.toDoubleOrNull() ?: 0.0 }
        ProductSortOption.BEST_SELLER ->
            products.sortedByDescending { it.rating }
        ProductSortOption.GROUP_BY_SUB_CATEGORY ->
            products.sortedBy { it.vendor }
    }
}
