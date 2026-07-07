package com.troves.domain.usecase.review

import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.usecase.address.GetSavedAddressesUseCase
import kotlinx.coroutines.flow.firstOrNull

data class ReviewerIdentity(val firstName: String, val lastName: String)

/**
 * Best-effort resolution of the current user's first/last name to pre-fill the review composer.
 * Firebase only stores a (usually null) displayName, so the real name — when available — comes
 * from the default Shopify address. Falls back to displayName, then the email local-part. The
 * user can always edit the pre-filled values before submitting.
 */
class GetReviewerIdentityUseCase(
    private val getSavedAddresses: GetSavedAddressesUseCase,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(): ReviewerIdentity {
        // 1) Default (or first) saved Shopify address — the only real first/last name source.
        val addresses = getSavedAddresses().firstOrNull().orEmpty()
        val address = addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()
        if (address != null && !(address.firstName.isNullOrBlank() && address.lastName.isNullOrBlank())) {
            return ReviewerIdentity(address.firstName.orEmpty(), address.lastName.orEmpty())
        }

        val profile = authenticationRepository.getCurrentUserProfile()

        // 2) Firebase displayName (usually null) split on the first space.
        profile?.displayName?.trim()?.takeIf { it.isNotEmpty() }?.let { name ->
            val parts = name.split(" ", limit = 2)
            return ReviewerIdentity(
                firstName = parts.getOrElse(0) { "" },
                lastName = parts.getOrElse(1) { "" },
            )
        }

        // 3) Email local-part as a last resort.
        profile?.email?.takeIf { it.isNotBlank() }?.let { email ->
            return ReviewerIdentity(firstName = email.substringBefore("@"), lastName = "")
        }

        return ReviewerIdentity("", "")
    }
}
