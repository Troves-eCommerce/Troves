package com.troves.domain.usecase.review

import com.troves.domain.entity.Review
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result

sealed interface SubmitReviewResult {
    data object Success : SubmitReviewResult
    data object RequiresLogin : SubmitReviewResult
    data class Error(val throwable: Throwable) : SubmitReviewResult
}

/**
 * Submits (creates or overwrites) the signed-in user's review for a product. The Firestore
 * document id is the user id, so re-submitting simply edits the existing review — enforcing
 * the "one review per user per product" rule.
 */
class SubmitReviewUseCase(
    private val repository: TrovesRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(
        productId: String,
        firstName: String,
        lastName: String,
        rating: Int,
        comment: String,
        createdAt: Long,
    ): SubmitReviewResult {
        if (!authenticationRepository.isLoggedIn()) return SubmitReviewResult.RequiresLogin
        val userId = authenticationRepository.getCurrentUserId()
            ?: return SubmitReviewResult.RequiresLogin

        val review = Review(
            id = userId,
            userId = userId,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            rating = rating,
            comment = comment.trim(),
            createdAt = createdAt,
        )

        return when (val result = repository.submitReview(productId, review)) {
            is Result.Success -> SubmitReviewResult.Success
            is Result.Error -> SubmitReviewResult.Error(result.throwable)
            Result.Loading -> SubmitReviewResult.Error(IllegalStateException("Unexpected loading state"))
        }
    }
}
