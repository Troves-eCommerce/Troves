package com.troves.data.repository

import com.troves.domain.Product
import com.troves.domain.Result
import com.troves.domain.home.Ad
import com.troves.domain.home.Brand
import com.troves.domain.home.Category
import com.troves.domain.home.HomeRepository
import kotlinx.coroutines.delay

/**
 * In-memory [HomeRepository] used while the real Shopify-backed implementation
 * is being built. Returns deterministic mock data and simulates a short network
 * delay so the Home screen's loading / shimmer state is exercised.
 *
 * Swap the Koin binding in `DataModule` for the real repository once it exists.
 */
class FakeHomeRepository : HomeRepository {

    override suspend fun getAds(): Result<List<Ad>> {
        delay(LOADING_DELAY_MS)
        return Result.Success(
            listOf(
                Ad(
                    id = 1,
                    titleTop = "30% DISCOUNT",
                    titleBottom = "Today special",
                    description = "Get discount for every order, only valid for today.",
                ),
                Ad(
                    id = 2,
                    titleTop = "NEW ARRIVALS",
                    titleBottom = "Summer 2026",
                    description = "Fresh styles just landed. Explore the latest collection.",
                ),
                Ad(
                    id = 3,
                    titleTop = "FREE SHIPPING",
                    titleBottom = "Orders over \$50",
                    description = "Shop more, save more with free delivery on big orders.",
                ),
            ),
        )
    }

    override suspend fun getBrands(): Result<List<Brand>> {
        delay(LOADING_DELAY_MS)
        return Result.Success(
            listOf(
                Brand(id = 1, name = "Nike"),
                Brand(id = 2, name = "Adidas"),
                Brand(id = 3, name = "Zara"),
                Brand(id = 4, name = "Puma"),
                Brand(id = 5, name = "H&M"),
                Brand(id = 6, name = "Gucci"),
            ),
        )
    }

    override suspend fun getCategories(): Result<List<Category>> {
        delay(LOADING_DELAY_MS)
        return Result.Success(
            listOf(
                Category(id = 1, name = "Men"),
                Category(id = 2, name = "Women"),
                Category(id = 3, name = "Kids"),
                Category(id = 4, name = "Shoes"),
            ),
        )
    }

    override suspend fun getJustForYouProducts(): Result<List<Product>> {
        delay(LOADING_DELAY_MS)
        return Result.Success(sampleProducts(idOffset = 0))
    }

    override suspend fun getTrendingProducts(): Result<List<Product>> {
        delay(LOADING_DELAY_MS)
        return Result.Success(sampleProducts(idOffset = 100))
    }

    private fun sampleProducts(idOffset: Long): List<Product> = listOf(
        Product(
            id = idOffset + 1,
            title = "Modern Leather Sneakers",
            vendor = "Nike",
            price = "120.00",
            imageUrl = null,
            status = "active",
        ),
        Product(
            id = idOffset + 2,
            title = "Classic Denim Jacket",
            vendor = "Levi's",
            price = "120.00",
            imageUrl = null,
            status = "active",
        ),
        Product(
            id = idOffset + 3,
            title = "Cotton Crewneck Tee",
            vendor = "Zara",
            price = "45.00",
            imageUrl = null,
            status = "active",
        ),
        Product(
            id = idOffset + 4,
            title = "Wool Blend Overcoat",
            vendor = "H&M",
            price = "210.00",
            imageUrl = null,
            status = "active",
        ),
    )

    private companion object {
        const val LOADING_DELAY_MS = 800L
    }
}
