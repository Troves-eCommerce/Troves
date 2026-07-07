package com.troves.presintation.ui.productDetails

import com.troves.domain.entity.Review
import kotlin.random.Random

/**
 * DEV-ONLY seed reviews so products look reviewed before launch.
 *
 * Generated deterministically from the product id (same product → same reviews every load),
 * merged with any real Firestore reviews in the ViewModel. Seed reviews use synthetic user ids
 * (`seed_*`) so they can never collide with, or be flagged as, the signed-in user's own review.
 *
 * To retire this once real reviews exist: set [ENABLED] = false (or delete this file and the
 * two call sites in ProductDetailsViewModel.loadReviews).
 */
object FakeReviews {

    /** Master switch — flip to false to disable all seed reviews. */
    const val ENABLED = true

    private const val DAY_MILLIS = 86_400_000L

    fun forProduct(productId: String, now: Long): List<Review> {
        if (!ENABLED || productId.isBlank()) return emptyList()

        val random = Random(productId.hashCode())
        val count = 3 + random.nextInt(4) // 3..6 reviews

        // Pick distinct authors/comments so a single product doesn't repeat itself.
        val names = NAMES.shuffled(random)
        val comments = COMMENTS.shuffled(random)

        return List(count) { index ->
            val (firstName, lastName) = names[index % names.size]
            ReviewUiSeed(
                index = index,
                productId = productId,
                firstName = firstName,
                lastName = lastName,
                rating = RATINGS[random.nextInt(RATINGS.size)],
                comment = comments[index % comments.size],
                daysAgo = 1 + random.nextInt(120),
                now = now,
            )
        }
    }

    private fun ReviewUiSeed(
        index: Int,
        productId: String,
        firstName: String,
        lastName: String,
        rating: Int,
        comment: String,
        daysAgo: Int,
        now: Long,
    ) = Review(
        id = "seed_${productId}_$index",
        userId = "seed_${productId}_$index",
        firstName = firstName,
        lastName = lastName,
        rating = rating,
        comment = comment,
        createdAt = now - daysAgo * DAY_MILLIS,
    )

    // Weighted toward 4–5 stars so products read positively, with the odd 3.
    private val RATINGS = listOf(5, 5, 5, 4, 4, 4, 5, 3, 4, 5)

    private val NAMES = listOf(
        "Ahmed" to "Hassan",
        "Sara" to "Khaled",
        "Mohamed" to "Ali",
        "Layla" to "Ibrahim",
        "Omar" to "Farouk",
        "Nour" to "Mansour",
        "Youssef" to "Saleh",
        "Mona" to "Adel",
        "Karim" to "Nabil",
        "Huda" to "Rashad",
        "Tarek" to "Zaki",
        "Dina" to "Samir",
    )

    private val COMMENTS = listOf(
        "Excellent quality, exactly as described. Highly recommend!",
        "Great value for the price. Would buy again.",
        "Fast delivery and the product looks even better in person.",
        "Very comfortable and well made. Happy with my purchase.",
        "Good product overall, the color is slightly different from the photos.",
        "Loved it! Exactly what I was looking for.",
        "Solid build quality and nice finish. Worth it.",
        "Arrived on time and packaged carefully. Five stars.",
        "Nice design, fits perfectly. Very satisfied.",
        "Decent for the price, does the job well.",
    )
}
