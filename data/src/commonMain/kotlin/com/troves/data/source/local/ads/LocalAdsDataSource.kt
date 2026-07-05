package com.troves.data.source.local.ads

import com.troves.domain.entity.Ad

interface LocalAdsDataSource {
    suspend fun getAds(): List<Ad>
}

class LocalAdsDataSourceImpl : LocalAdsDataSource {
    override suspend fun getAds(): List<Ad> {
        return listOf(
            Ad(
                id = 1,
                titleTop = "NIKE SALE",
                titleBottom = "30% DISCOUNT",
                description = "Get a massive discount on all Nike products. Valid for today only!",
                targetType = "brand",
                targetId = "gid://shopify/Collection/435118702847", // Nike collection ID
                targetName = "Nike",
            ),
            Ad(
                id = 2,
                titleTop = "PUMA NEW",
                titleBottom = "Summer 2026",
                description = "Fresh Puma styles just landed. Explore the latest collection.",
                targetType = "brand",
                targetId = "gid://shopify/Collection/435118833919", // Puma collection ID
                targetName = "Puma",
            ),
            Ad(
                id = 3,
                titleTop = "ADIDAS FREE",
                titleBottom = "Free Shipping",
                description = "Shop Adidas and save more with free delivery on big orders.",
                targetType = "brand",
                targetId = "gid://shopify/Collection/435118735615", // Adidas collection ID
                targetName = "Adidas",
            ),
        )
    }
}
