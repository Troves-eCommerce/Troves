package com.troves.presintation.ui.productDetails.models

data class ReviewUi(
    val id: String = "",
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val date: String = "",        // pre-formatted for display
    val isMine: Boolean = false,  // drives the highlight + "Your review" badge
) {
    val authorName: String
        get() = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { "Anonymous" }
}

/** Editable draft backing the review composer. */
data class ReviewDraft(
    val firstName: String = "",
    val lastName: String = "",
    val rating: Int = 0,
    val comment: String = "",
)
