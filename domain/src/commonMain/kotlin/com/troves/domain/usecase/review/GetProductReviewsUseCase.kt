package com.troves.domain.usecase.review

import com.troves.domain.entity.Review
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result

/** Reviews for a product plus the id of the currently signed-in user (null if none). */
data class ReviewsResult(
    val reviews: List<Review>,
    val currentUserId: String?,
)

/**
 * Fetches a product's reviews. Reads are public — not login gated — but we also surface the
 * current user id so the caller can flag which review (if any) belongs to the signed-in user.
 */
class GetProductReviewsUseCase(
    private val repository: TrovesRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(productId: String): Result<ReviewsResult> =
        when (val result = repository.getReviews(productId)) {
            is Result.Success -> Result.Success(
                ReviewsResult(
                    reviews = result.value,
                    currentUserId = authenticationRepository.getCurrentUserId(),
                )
            )

            is Result.Error -> Result.Error(result.throwable)
            Result.Loading -> Result.Loading
        }
}
